package cn.fuguang.order.biz;

import cn.fuguang.order.pojo.vo.req.SeckillCouponReq;
import cn.fuguang.order.pojo.vo.res.SeckillCouponRes;

public interface SeckillBiz {

    /**
     * 执行优惠券秒杀
     * @param req 秒杀请求参数
     * @return 秒杀结果
     */
    SeckillCouponRes doSeckill(SeckillCouponReq req);
}
