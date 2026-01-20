package cn.fuguang.order.service.impl;

import cn.fuguang.api.order.dto.req.PayCompleteReqDTO;
import cn.fuguang.order.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知服务实现类
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendOrderNotification(PayCompleteReqDTO reqDTO) {
        log.info("开始发送订单通知，订单号：{}", reqDTO.getOrderNo());
        
        // 模拟客户手机号和ID
        String mobile = "13800138000";
        Long customerId = 1001L;
        
        // 构建通知内容
        String content = String.format("您的订单已支付完成！订单号：%s，实际支付金额：%.2f元，支付方式：%s",
                reqDTO.getOrderNo(), reqDTO.getActualPayAmount() / 100.0, reqDTO.getPayMethod());
        
        // 发送短信通知
        boolean smsResult = sendSmsNotification(mobile, content);
        log.info("订单短信通知发送{}，订单号：{}，手机号：{}", smsResult ? "成功" : "失败", reqDTO.getOrderNo(), mobile);
        
        // 发送站内信通知
        boolean inAppResult = sendInAppNotification(customerId, content);
        log.info("订单站内信通知发送{}，订单号：{}，客户ID：{}", inAppResult ? "成功" : "失败", reqDTO.getOrderNo(), customerId);
        
        log.info("订单通知发送完成，订单号：{}", reqDTO.getOrderNo());
    }

    @Override
    public boolean sendSmsNotification(String mobile, String content) {
        log.info("开始模拟发送短信通知，手机号：{}，内容：{}", mobile, content);
        
        // 模拟短信发送延迟
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            log.error("模拟短信发送延迟异常", e);
        }
        
        // 模拟短信发送成功
        log.info("模拟短信发送成功，手机号：{}", mobile);
        return true;
    }

    @Override
    public boolean sendInAppNotification(Long customerId, String content) {
        log.info("开始模拟发送站内信通知，客户ID：{}，内容：{}", customerId, content);
        
        // 模拟站内信发送延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            log.error("模拟站内信发送延迟异常", e);
        }
        
        // 模拟站内信发送成功
        log.info("模拟站内信发送成功，客户ID：{}", customerId);
        return true;
    }
}
