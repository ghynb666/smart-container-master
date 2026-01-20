package cn.fuguang.order.service;

import cn.fuguang.api.order.dto.req.PayCompleteReqDTO;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 发送订单通知
     * @param reqDTO 支付完成请求参数，包含订单信息和支付信息
     */
    void sendOrderNotification(PayCompleteReqDTO reqDTO);
    
    /**
     * 发送短信通知
     * @param mobile 手机号
     * @param content 短信内容
     */
    boolean sendSmsNotification(String mobile, String content);
    
    /**
     * 发送站内信通知
     * @param customerId 客户ID
     * @param content 站内信内容
     */
    boolean sendInAppNotification(Long customerId, String content);
}
