package cn.fuguang.order.service;

import java.util.List;
import java.util.Map;

/**
 * 消费记录服务
 */
public interface ConsumptionRecordService {

    /**
     * 生成消费记录
     * @param orderNo 订单号
     * @param customerId 客户ID
     * @param deviceId 设备ID
     * @param payAmount 支付金额（分）
     * @param purchasedGoods 购买的商品信息，key为商品ID，value为购买数量
     * @return 是否生成成功
     */
    boolean generateConsumptionRecord(String orderNo, String customerId, String deviceId, long payAmount, Map<String, Integer> purchasedGoods);
    
    /**
     * 查询客户消费记录
     * @param customerId 客户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 消费记录列表
     */
    List<Map<String, Object>> queryConsumptionRecords(String customerId, int pageNum, int pageSize);
}