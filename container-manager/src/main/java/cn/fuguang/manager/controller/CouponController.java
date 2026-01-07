package cn.fuguang.manager.controller;

import cn.fuguang.manager.biz.CouponBiz;
import cn.fuguang.manager.pojo.vo.req.CouponConfigReq;
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
@RequestMapping("/api/manager/coupon")
@Slf4j
public class CouponController {

    // TODO: 2024/8/6 入参，出参应该用aop切面实现 暂时先硬编码

    @Resource
    private CouponBiz couponBiz;

    @PostMapping("/addCouponConfig")
    public BaseResult<Void> addCouponConfig(@RequestBody @Valid CouponConfigReq req){
        log.info("[coupon] addCouponConfig req:{}", JSONObject.toJSONString(req));
        try {
            couponBiz.addCouponConfig(req);
        } catch (Exception e) {
            log.error("[coupon] addCouponConfig exception" + e);
            return BaseResult.fail();
        }
        log.info("[coupon] addCouponConfig success");
        return BaseResult.success();
    }

}
