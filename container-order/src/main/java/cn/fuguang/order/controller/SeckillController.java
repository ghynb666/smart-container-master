package cn.fuguang.order.controller;

import cn.fuguang.order.biz.SeckillBiz;
import cn.fuguang.order.pojo.vo.req.SeckillCouponReq;
import cn.fuguang.order.pojo.vo.res.SeckillCouponRes;
import cn.fuguang.web.BaseResult;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("seckill")
@Slf4j
public class SeckillController {

    @Resource
    private SeckillBiz seckillBiz;

    /**
     * 优惠券秒杀接口
     */
    @PostMapping("/api/seckill/doSeckill")
    public BaseResult<SeckillCouponRes> doSeckill(@RequestBody @Valid SeckillCouponReq req) {
        log.info("优惠券秒杀请求 req:{}", JSONObject.toJSONString(req));
        SeckillCouponRes res;
        try {
            res = seckillBiz.doSeckill(req);
        } catch (Exception e) {
            log.error("优惠券秒杀系统异常" + e);
            return BaseResult.fail("系统异常, 请稍后重试");
        }
        log.info("优惠券秒杀结果 res:{}", JSONObject.toJSONString(res));
        return BaseResult.success(res);
    }
}
