package cn.fuguang.device.processor.deviceEvent;

import cn.fuguang.api.order.OrderFeignService;
import cn.fuguang.api.order.dto.req.PayCompleteReqDTO;
import cn.fuguang.device.service.WeightCompareService;
import cn.fuguang.enums.EventTypeEnum;
import cn.fuguang.pojo.bean.DeviceWeightBean;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DeviceCloseGateEventProcessor extends DeviceEventAbstractProcessor {
    
    @Resource
    private WeightCompareService weightCompareService;
    
    @Resource
    private OrderFeignService orderFeignService;
    
    @Override
    public String getProcessorType() {
        return EventTypeEnum.CLOSE_GATE.name();
    }

    @Override
    public void doDeviceEvent(JSONObject params) {
        log.info("开始处理关门事件，参数：{}", params);
        
        // 解析关门事件参数
        String deviceId = params.getString("deviceId");
        String gateId = params.getString("gateId");
        String orderNo = params.getString("orderNo");
        
        if (deviceId == null || gateId == null || orderNo == null) {
            log.error("关门事件参数不完整，deviceId：{}gateId：{}orderNo：{}", deviceId, gateId, orderNo);
            return;
        }
        
        // 模拟关门检测
        log.info("检测到设备关门，设备ID：{}仓门ID：{}订单号：{}", deviceId, gateId, orderNo);
        
        // 模拟获取开门前重量信息（实际应该从缓存中获取）
        DeviceWeightBean beforeWeight = simulateBeforeWeight(deviceId, gateId);
        
        // 模拟获取关门后重量信息
        DeviceWeightBean afterWeight = simulateAfterWeight(deviceId, gateId);
        
        // 调用重量对比服务识别购买的商品
        Map<String, Integer> purchasedGoods = weightCompareService.compareWeightAndIdentifyGoods(deviceId, gateId, beforeWeight, afterWeight);
        
        if (!purchasedGoods.isEmpty()) {
            // 模拟计算实际支付金额（这里简单模拟，实际应该根据商品价格计算）
            long actualPayAmount = simulateCalculatePayAmount(purchasedGoods);
            
            // 调用订单服务完成支付
            log.info("准备调用订单服务完成支付，订单号：{}实际支付金额：{}分", orderNo, actualPayAmount);
            
            try {
                orderFeignService.payComplete(PayCompleteReqDTO.builder()
                        .orderNo(orderNo)
                        .actualPayAmount(actualPayAmount)
                        .payMethod("ALIPAY")
                        .paySerialNo("PAY_" + System.currentTimeMillis())
                        .purchasedGoodsJson(JSONObject.toJSONString(purchasedGoods))
                        .build());
                log.info("调用订单服务完成支付成功，订单号：{}", orderNo);
            } catch (Exception e) {
                log.error("调用订单服务完成支付失败，订单号：{}", orderNo, e);
            }
        } else {
            log.info("未识别到购买商品，订单号：{}", orderNo);
        }
        
        log.info("关门事件处理完成，订单号：{}", orderNo);
    }
    
    /**
     * 模拟开门前重量信息
     */
    private DeviceWeightBean simulateBeforeWeight(String deviceId, String gateId) {
        DeviceWeightBean weightBean = new DeviceWeightBean();
        weightBean.setDeviceId(deviceId);
        weightBean.setGateId(gateId);
        
        // 模拟货道重量数据（克）
        Map<Integer, BigDecimal> weightMap = new HashMap<>();
        weightMap.put(1, new BigDecimal(1000)); // 货道1：1000g
        weightMap.put(2, new BigDecimal(800));  // 货道2：800g
        weightMap.put(3, new BigDecimal(600));  // 货道3：600g
        
        weightBean.setWeightMap(weightMap);
        return weightBean;
    }
    
    /**
     * 模拟关门后重量信息
     */
    private DeviceWeightBean simulateAfterWeight(String deviceId, String gateId) {
        DeviceWeightBean weightBean = new DeviceWeightBean();
        weightBean.setDeviceId(deviceId);
        weightBean.setGateId(gateId);
        
        // 模拟货道重量数据（克），假设用户取走了货道1的一个商品（减少350g）
        Map<Integer, BigDecimal> weightMap = new HashMap<>();
        weightMap.put(1, new BigDecimal(650));  // 货道1：650g（减少了350g）
        weightMap.put(2, new BigDecimal(800));  // 货道2：800g（不变）
        weightMap.put(3, new BigDecimal(600));  // 货道3：600g（不变）
        
        weightBean.setWeightMap(weightMap);
        return weightBean;
    }
    
    /**
     * 模拟计算实际支付金额
     */
    private long simulateCalculatePayAmount(Map<String, Integer> purchasedGoods) {
        // 模拟商品价格映射，key为商品ID，value为商品价格（分）
        Map<String, Long> productPriceMap = new HashMap<>();
        productPriceMap.put("PROD001", 350L); // 可乐 3.5元
        productPriceMap.put("PROD002", 200L); // 矿泉水 2元
        productPriceMap.put("PROD003", 500L); // 饼干 5元
        productPriceMap.put("PROD004", 600L); // 薯片 6元
        productPriceMap.put("PROD005", 450L); // 方便面 4.5元
        
        // 计算总金额
        long totalAmount = 0;
        for (Map.Entry<String, Integer> entry : purchasedGoods.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            Long price = productPriceMap.getOrDefault(productId, 0L);
            totalAmount += price * quantity;
        }
        
        return totalAmount;
    }
}
