package cn.fuguang.device.service;

import cn.fuguang.pojo.bean.DeviceWeightBean;
import java.util.Map;

/**
 * 重量对比与商品识别服务
 */
public interface WeightCompareService {

    /**
     * 比较开门前后的重量，识别用户购买的商品
     * @param deviceId 设备ID
     * @param gateId 仓门ID
     * @param beforeWeight 开门前重量信息
     * @param afterWeight 关门后重量信息
     * @return 购买的商品信息，key为商品ID，value为购买数量
     */
    Map<String, Integer> compareWeightAndIdentifyGoods(String deviceId, String gateId, DeviceWeightBean beforeWeight, DeviceWeightBean afterWeight);

    /**
     * 模拟生成商品识别结果
     * @param deviceId 设备ID
     * @param gateId 仓门ID
     * @return 模拟的商品识别结果
     */
    Map<String, Integer> simulateGoodsIdentification(String deviceId, String gateId);
}