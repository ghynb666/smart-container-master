package cn.fuguang.order.biz.impl;

import cn.fuguang.api.channel.AliPayFeignService;
import cn.fuguang.api.channel.dto.req.AgreementSignReqDTO;
import cn.fuguang.api.channel.dto.res.AgreementSignResDTO;
import cn.fuguang.api.device.DeviceInfoFeignService;
import cn.fuguang.api.device.req.CheckDeviceStatusReqDTO;
import cn.fuguang.api.order.dto.req.AgreementSignCallBackReqDTO;
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
import cn.fuguang.order.service.CustomerService;
import cn.fuguang.order.service.OrderService;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson2.JSONObject;
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

        //创建订单前置校验
        createOrderCheckParams(req);

        //加入分布式锁
        String redisLockKey = RedisConstants.REDIS_LOCK_CREATE_ORDER_INDEX + req.getDeviceId() + "_" + req.getGateId();
        RLock lock = redissonClient.getLock(redisLockKey);

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
                    //释放锁
                    lock.unlock();
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
        return scanCreateOrderRes;
    }

    @Override
    public void agreementSignCallBack(AgreementSignCallBackReqDTO reqDTO) {
        String orderNo = reqDTO.getOrderNo();
        OrderInfoEntity orderInfoEntity = orderService.queryByOrderNo(orderNo);

        if (AgreementSignStatusEnum.NORMAL.name().equals(reqDTO.getAgreementStatus())) {
            //签约成功 更新订单状态
            orderService.updateOrderStatus(orderNo, OrderStatusEnum.PRE_AUTH_SUCCESS.name());

            //调用设备服务开仓门

        } else {
            //签约失败 更新订单状态
            orderService.updateOrderStatus(orderNo, OrderStatusEnum.PRE_AUTH_FAIL.name());

            //释放分布式锁
            String redisLockKey = RedisConstants.REDIS_LOCK_CREATE_ORDER_INDEX + orderInfoEntity.getDeviceId() + "_" + orderInfoEntity.getGateId();
            RLock lock = redissonClient.getLock(redisLockKey);
            try {
                lock.unlock();
            } catch (Exception e) {
                log.error(MonitorConstants.REDISSON_UN_LOCK_ERROR + "释放仓门分布式锁异常 redisLockKey:{}", redisLockKey);
            }
        }
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
