package cn.fuguang.order.service.impl;

import cn.fuguang.order.service.CouponPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 优惠券/积分服务实现类
 */
@Service
@Slf4j
public class CouponPointServiceImpl implements CouponPointService {

    // 模拟客户积分数据，key为客户ID，value为积分数量
    private static final Map<String, Integer> CUSTOMER_POINTS_MAP = new HashMap<>();
    
    // 模拟客户优惠券数据，key为客户ID，value为该客户的优惠券列表
    private static final Map<String, Map<String, Integer>> CUSTOMER_COUPONS_MAP = new HashMap<>();
    
    static {
        // 初始化模拟数据
        // 客户CUST001有100积分
        CUSTOMER_POINTS_MAP.put("CUST001", 100);
        CUSTOMER_POINTS_MAP.put("CUST002", 50);
        CUSTOMER_POINTS_MAP.put("CUST003", 200);
        
        // 客户优惠券
        Map<String, Integer> couponsMap = new HashMap<>();
        couponsMap.put("COUPON001", 1); // 满10减2优惠券 1张
        couponsMap.put("COUPON002", 2); // 满20减5优惠券 2张
        
        CUSTOMER_COUPONS_MAP.put("CUST001", couponsMap);
        CUSTOMER_COUPONS_MAP.put("CUST002", new HashMap<>(couponsMap));
        CUSTOMER_COUPONS_MAP.put("CUST003", new HashMap<>(couponsMap));
    }

    @Override
    public boolean useCoupon(String customerId, String couponId) {
        log.info("开始使用优惠券，客户ID：{}优惠券ID：{}", customerId, couponId);
        
        // 获取客户优惠券
        Map<String, Integer> couponsMap = CUSTOMER_COUPONS_MAP.computeIfAbsent(customerId, k -> new HashMap<>());
        
        // 检查优惠券是否存在且数量充足
        Integer couponCount = couponsMap.get(couponId);
        if (couponCount == null || couponCount <= 0) {
            log.error("优惠券不存在或已用完，客户ID：{}优惠券ID：{}", customerId, couponId);
            return false;
        }
        
        // 使用优惠券（数量减1）
        couponsMap.put(couponId, couponCount - 1);
        log.info("优惠券使用成功，客户ID：{}优惠券ID：{}剩余数量：{}", 
                customerId, couponId, couponsMap.get(couponId));
        
        return true;
    }

    @Override
    public boolean addPoints(String customerId, int points) {
        log.info("开始增加积分，客户ID：{}增加积分：{}", customerId, points);
        
        // 获取当前积分
        int currentPoints = CUSTOMER_POINTS_MAP.getOrDefault(customerId, 0);
        
        // 增加积分
        int newPoints = currentPoints + points;
        CUSTOMER_POINTS_MAP.put(customerId, newPoints);
        
        log.info("积分增加成功，客户ID：{}当前积分：{}增加积分：{}新积分：{}", 
                customerId, currentPoints, points, newPoints);
        
        return true;
    }

    @Override
    public boolean deductPoints(String customerId, int points) {
        log.info("开始扣减积分，客户ID：{}扣减积分：{}", customerId, points);
        
        // 获取当前积分
        int currentPoints = CUSTOMER_POINTS_MAP.getOrDefault(customerId, 0);
        
        // 检查积分是否充足
        if (currentPoints < points) {
            log.error("积分不足，客户ID：{}当前积分：{}扣减积分：{}", customerId, currentPoints, points);
            return false;
        }
        
        // 扣减积分
        int newPoints = currentPoints - points;
        CUSTOMER_POINTS_MAP.put(customerId, newPoints);
        
        log.info("积分扣减成功，客户ID：{}当前积分：{}扣减积分：{}新积分：{}", 
                customerId, currentPoints, points, newPoints);
        
        return true;
    }

    @Override
    public int getCustomerPoints(String customerId) {
        int points = CUSTOMER_POINTS_MAP.getOrDefault(customerId, 0);
        log.info("查询客户积分，客户ID：{}积分：{}", customerId, points);
        return points;
    }
}