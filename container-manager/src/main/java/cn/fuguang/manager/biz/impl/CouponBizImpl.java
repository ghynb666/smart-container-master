package cn.fuguang.manager.biz.impl;

import cn.fuguang.entity.CouponConfigEntity;
import cn.fuguang.manager.biz.CouponBiz;
import cn.fuguang.manager.pojo.vo.req.CouponConfigReq;
import cn.fuguang.manager.service.CouponService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CouponBizImpl implements CouponBiz {

    @Resource
    private CouponService couponService;

    @Override
    public void addCouponConfig(CouponConfigReq req) {
        CouponConfigEntity couponConfigEntity = new CouponConfigEntity();
        BeanUtil.copyProperties(req, couponConfigEntity);
        couponConfigEntity.setCouponConfigId(IdUtil.simpleUUID());
        couponService.insert(couponConfigEntity);
    }
}
