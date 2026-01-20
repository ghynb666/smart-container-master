package cn.fuguang.order.biz.impl;

import cn.fuguang.api.channel.AliPayFeignService;
import cn.fuguang.api.channel.dto.req.AgreementSignReqDTO;
import cn.fuguang.api.channel.dto.res.AgreementSignResDTO;
import cn.fuguang.api.device.DeviceInfoFeignService;
import cn.fuguang.api.device.DeviceOperateFeignService;
import cn.fuguang.api.device.req.CheckDeviceStatusReqDTO;
import cn.fuguang.api.device.req.CreateOrderOpenGateReqDTO;
import cn.fuguang.api.order.dto.req.AgreementSignCallBackReqDTO;
import cn.fuguang.api.order.dto.req.PayCompleteReqDTO;
import cn.fuguang.common.rocketmq.service.RocketMqService;
import cn.fuguang.constants.BaseConstants;
import cn.fuguang.constants.MonitorConstants;
import cn.fuguang.constants.RedisConstants;
import cn.fuguang.constants.RocketMQConstants;
import cn.fuguang.entity.CustomerEntity;
import cn.fuguang.entity.OrderInfoEntity;
import cn.fuguang.enums.AgreementSignStatusEnum;
import cn.fuguang.enums.OrderStatusEnum;
import cn.fuguang.enums.OrderTypeEnum;
import cn.fuguang.enums.ProcedureTypeEnum;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.feign.BaseResponse;
import cn.fuguang.order.biz.OrderBiz;
import cn.fuguang.order.pojo.vo.req.ScanCreateOrderReq;
import cn.fuguang.order.pojo.vo.res.ScanCreateOrderRes;
import cn.fuguang.order.service.BlackCustomerService;
import cn.fuguang.order.service.ConsumptionRecordService;
import cn.fuguang.order.service.CouponPointService;
import cn.fuguang.order.service.CustomerService;
import cn.fuguang.order.service.InventoryService;
import cn.fuguang.order.service.NotificationService;
import cn.fuguang.order.service.OrderService;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson2.JSONObject;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderBizImpl implements OrderBiz {

    @Resource
    private OrderService orderService;
    @Resource
    private BlackCustomerService blackCustomerService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private CustomerService customerService;
    @Resource
    private AliPayFeignService aliPayFeignService;
    @Resource
    private RocketMqService rocketMqService;
    @Resource
    private DeviceInfoFeignService deviceInfoFeignService;


    @Override
    public ScanCreateOrderRes scanCreateOrder(ScanCreateOrderReq req) {

        ScanCreateOrderRes scanCreateOrderRes = new ScanCreateOrderRes();
        RLock lock = null;
        String redisLockKey = null;

        try {
            //创建订单前置校验
            createOrderCheckParams(req);

            //加入分布式锁
            redisLockKey = RedisConstants.REDIS_LOCK_CREATE_ORDER_INDEX + req.getDeviceId() + "_" + req.getGateId();
            lock = redissonClient.getLock(redisLockKey);

            if (lock.tryLock()) {
                //生成订单号
                String orderNo = orderService.createOrderNo(OrderTypeEnum.SHOP_ORDER);

                //获取客户信息
                CustomerEntity customerEntity = customerService.queryCustomerById(req.getCustomerId());

                //创建订单详情
                OrderInfoEntity orderInfo = orderService.buildOrderInfo(req, orderNo);

                log.info("用户扫码下单生成订单入库 orderInfo:{}", JSONObject.toJSONString(orderInfo));
                //入库
                orderService.saveOrderInfo(orderInfo);

                //微信支付宝预授权下单
                if (ProcedureTypeEnum.ALI.name().equals(req.getSourceType())){
                    BaseResponse<AgreementSignResDTO> agreementSignResDTOBaseResponse = aliPayFeignService.agreementSign(AgreementSignReqDTO.builder().orderNo(orderNo)
                            .customerId(customerEntity.getId())
                            .mobile(customerEntity.getMobile()).build());

                    if (!agreementSignResDTOBaseResponse.isSuccess()){
                        log.error("调用channel服务创建阿里预授权订单失败");
                        //更新订单状态
                        orderService.updateOrderStatus(orderNo, OrderStatusEnum.PRE_AUTH_CREATE_ERROR.name());
                    } else {
                        //更新订单状态
                        orderService.updateOrderStatus(orderNo, OrderStatusEnum.PRE_AUTH_CREATE_SUCCESS.name());
                        scanCreateOrderRes.setSignStr(agreementSignResDTOBaseResponse.getData().getSignStr());
                    }
                }

                //发送延迟消息
                this.sendDelayMessage(orderNo);
                scanCreateOrderRes.setOrderNo(orderNo);

            } else {
                log.error("用户扫码下单获取redis锁失败");
                throw ContainerException.REDIS_LOCK_ERROR.newInstance("有客户正在购买商品,请稍后重试");
            }
        } finally {
            //确保在所有情况下都能释放锁
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                    log.info("分布式锁释放成功，锁键：{}", redisLockKey);
                } catch (Exception e) {
                    log.error(MonitorConstants.REDISSON_UN_LOCK_ERROR + "释放分布式锁异常 redisLockKey:{}", redisLockKey, e);
                }
            }
        }
        return scanCreateOrderRes;
    }

    @Resource
    private DeviceOperateFeignService deviceOperateFeignService;

    @Override
    public void agreementSignCallBack(AgreementSignCallBackReqDTO reqDTO) {
        String orderNo = reqDTO.getOrderNo();
        OrderInfoEntity orderInfoEntity = orderService.queryByOrderNo(orderNo);

        if (AgreementSignStatusEnum.NORMAL.name().equals(reqDTO.getAgreementStatus())) {
            //签约成功 更新订单状态
            orderService.updateOrderStatus(orderNo, OrderStatusEnum.PRE_AUTH_SUCCESS.name());

            //调用设备服务开仓门
            log.info("预授权成功，开始调用设备服务开仓门，订单号：{}", orderNo);
            try {
                CreateOrderOpenGateReqDTO openGateReq = new CreateOrderOpenGateReqDTO();
                openGateReq.setDeviceId(orderInfoEntity.getDeviceId());
                openGateReq.setGateId(orderInfoEntity.getGateId());
                openGateReq.setOrderNo(orderNo);
                deviceOperateFeignService.createOrderOpenGate(openGateReq);
                log.info("调用设备服务开仓门成功，订单号：{}", orderNo);
            } catch (Exception e) {
                log.error("调用设备服务开仓门失败，订单号：{}", orderNo, e);
            }

            //释放分布式锁
            String redisLockKey = RedisConstants.REDIS_LOCK_CREATE_ORDER_INDEX + orderInfoEntity.getDeviceId() + "_" + orderInfoEntity.getGateId();
            RLock lock = redissonClient.getLock(redisLockKey);
            try {
                lock.unlock();
                log.info("释放分布式锁成功，锁键：{}", redisLockKey);
            } catch (Exception e) {
                log.error(MonitorConstants.REDISSON_UN_LOCK_ERROR + "释放仓门分布式锁异常 redisLockKey:{}", redisLockKey);
            }
        } else {
            //签约失败 更新订单状态
            orderService.updateOrderStatus(orderNo, OrderStatusEnum.PRE_AUTH_FAIL.name());

            //释放分布式锁
            String redisLockKey = RedisConstants.REDIS_LOCK_CREATE_ORDER_INDEX + orderInfoEntity.getDeviceId() + "_" + orderInfoEntity.getGateId();
            RLock lock = redissonClient.getLock(redisLockKey);
            try {
                lock.unlock();
                log.info("预授权失败，释放分布式锁成功，锁键：{}", redisLockKey);
            } catch (Exception e) {
                log.error(MonitorConstants.REDISSON_UN_LOCK_ERROR + "释放仓门分布式锁异常 redisLockKey:{}", redisLockKey);
            }
        }
    }

    @Resource
    private InventoryService inventoryService;
    
    @Resource
    private ConsumptionRecordService consumptionRecordService;
    
    @Resource
    private CouponPointService couponPointService;
    
    @Resource
    private NotificationService notificationService;
    
    @Override
    public void handlePayComplete(PayCompleteReqDTO reqDTO) {
        String orderNo = reqDTO.getOrderNo();
        log.info("开始处理支付完成，订单号：{}", orderNo);
        
        // 查询订单信息
        OrderInfoEntity orderInfoEntity = orderService.queryByOrderNo(orderNo);
        if (orderInfoEntity == null) {
            log.error("处理支付完成失败，订单不存在，订单号：{}", orderNo);
            return;
        }
        
        // 更新订单状态为已支付
        log.info("更新订单状态为已支付，订单号：{}", orderNo);
        orderService.updateOrderStatus(orderNo, OrderStatusEnum.FULLY_PAY.name());
        
        // 模拟记录支付信息
        log.info("模拟记录支付信息，订单号：{}实际支付金额：{}分，支付方式：{}支付流水号：{}", 
                orderNo, reqDTO.getActualPayAmount(), reqDTO.getPayMethod(), reqDTO.getPaySerialNo());
        
        // 处理购买商品信息
        if (reqDTO.getPurchasedGoodsJson() != null) {
            log.info("处理购买商品信息，订单号：{}商品信息：{}", orderNo, reqDTO.getPurchasedGoodsJson());
            
            // 解析购买商品信息
            Map<String, Integer> purchasedGoods = JSONObject.parseObject(reqDTO.getPurchasedGoodsJson(), Map.class);
            
            // 扣减库存
            boolean deductResult = inventoryService.deductInventory(orderInfoEntity.getDeviceId(), purchasedGoods);
            if (deductResult) {
                log.info("库存扣减成功，订单号：{}", orderNo);
            } else {
                log.error("库存扣减失败，订单号：{}", orderNo);
            }
            
            // 生成消费记录
            boolean recordResult = consumptionRecordService.generateConsumptionRecord(
                    orderNo, 
                    orderInfoEntity.getCustomerId(), 
                    orderInfoEntity.getDeviceId(), 
                    reqDTO.getActualPayAmount(), 
                    purchasedGoods);
            if (recordResult) {
                log.info("消费记录生成成功，订单号：{}", orderNo);
            } else {
                log.error("消费记录生成失败，订单号：{}", orderNo);
            }
            
            // 模拟增加积分（每消费1元增加1积分）
            int pointsToAdd = (int) (reqDTO.getActualPayAmount() / 100);
            boolean pointsResult = couponPointService.addPoints(orderInfoEntity.getCustomerId(), pointsToAdd);
            if (pointsResult) {
                log.info("积分增加成功，客户ID：{}增加积分：{}", orderInfoEntity.getCustomerId(), pointsToAdd);
            } else {
                log.error("积分增加失败，客户ID：{}", orderInfoEntity.getCustomerId());
            }
        }
        
        // 发送订单通知
        notificationService.sendOrderNotification(reqDTO);
        
        log.info("支付完成处理成功，订单号：{}", orderNo);
    }

    private void sendDelayMessage(String orderNo) {
        int retryTimes = 0;
        boolean sendSuccess = false;
        while (!sendSuccess && retryTimes < BaseConstants.UPDATE_ORDER_STATUS_MAX_RETRY){
            try {
                rocketMqService.sendDelayMessage(RocketMQConstants.CREATE_ORDER_DELAY_TOPIC, orderNo, 5);
                sendSuccess = true;
            } catch (Exception e) {
                retryTimes++;
                log.info("创建订单成功:{}发送延迟消息失败,重试第:{}次", orderNo, retryTimes);
                ThreadUtil.sleep(1, TimeUnit.SECONDS);
            }
        }

        if (!sendSuccess){
            //发送时报报警,不阻断后续流程
            log.error(MonitorConstants.CREATE_ORDER_SEND_ERROR + "创建订单成功:{}发送延迟消息失败,重试超过最大次数", orderNo);
        }
    }

    private void createOrderCheckParams(ScanCreateOrderReq req){
        //校验用户是否存在
        customerService.checkCustomerExist(req.getCustomerId());
        //检查是否有未支付的订单
        orderService.checkCustomerUnpaidOrder(req.getCustomerId());
        //检查客户是否在黑名单中
        blackCustomerService.checkCustomer(req.getCustomerId());
        //检查参数设备状态，仓门状态
        deviceInfoFeignService.checkDeviceStatus(CheckDeviceStatusReqDTO.builder().deviceId(req.getDeviceId()).gateId(req.getGateId()).build());
    }
}
