package cn.fuguang.order.service.impl;

import cn.fuguang.constants.BaseConstants;
import cn.fuguang.entity.VerifyCodeEntity;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.order.mapper.VerifyCodeMapper;
import cn.fuguang.order.service.VerifyCodeService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {

    @Resource
    private VerifyCodeMapper verifyCodeMapper;

    @Override
    public String createVerifyCode() {
        return RandomUtil.randomNumbers(6);
    }

    @Override
    public VerifyCodeEntity buildVerifyCodeEntity(String mobile, String verifyCode, String orderNo) {

        VerifyCodeEntity verifyCodeEntity = new VerifyCodeEntity();
        verifyCodeEntity.setMobile(mobile);
        verifyCodeEntity.setVerifyCode(verifyCode);
        verifyCodeEntity.setOrderNo(orderNo);
        verifyCodeEntity.setRetryTimes(0);
        verifyCodeEntity.setInvalidTime(DateUtil.offsetMinute(new Date(), BaseConstants.VERIFY_CODE_PERIOD));
        return verifyCodeEntity;
    }

    @Override
    public void save(VerifyCodeEntity verifyCodeEntity) {
        try {
            verifyCodeMapper.insert(verifyCodeEntity);
        } catch (Exception e) {
            throw ContainerException.DATABASE_INERT_ERROR;
        }
    }

}
