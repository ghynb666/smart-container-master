package cn.fuguang.manager.service.impl;

import cn.fuguang.entity.CouponConfigEntity;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.manager.mapper.CouponMapper;
import cn.fuguang.manager.service.CouponService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    @Resource
    private CouponMapper couponMapper;
    @Override
    public void insert(CouponConfigEntity couponConfigEntity) {
        try {
            couponMapper.insert(couponConfigEntity);
        } catch (Exception e) {
            log.error("[coupon] insert error couponConfigEntity:" + JSONObject.toJSONString(couponConfigEntity), e);
            throw ContainerException.DATABASE_INERT_ERROR.newInstance("[coupon] insert error");
        }
    }
}
