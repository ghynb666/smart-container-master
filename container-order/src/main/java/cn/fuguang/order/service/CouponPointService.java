package cn.fuguang.order.service;

/**
 * 优惠券/积分服务
 */
public interface CouponPointService {

    /**
     * 处理优惠券使用
     * @param customerId 客户ID
     * @param couponId 优惠券ID
     * @return 是否使用成功
     */
    boolean useCoupon(String customerId, String couponId);
    
    /**
     * 增加客户积分
     * @param customerId 客户ID
     * @param points 增加的积分数量
     * @return 是否增加成功
     */
    boolean addPoints(String customerId, int points);
    
    /**
     * 扣减客户积分
     * @param customerId 客户ID
     * @param points 扣减的积分数量
     * @return 是否扣减成功
     */
    boolean deductPoints(String customerId, int points);
    
    /**
     * 查询客户积分
     * @param customerId 客户ID
     * @return 客户积分数量
     */
    int getCustomerPoints(String customerId);
}