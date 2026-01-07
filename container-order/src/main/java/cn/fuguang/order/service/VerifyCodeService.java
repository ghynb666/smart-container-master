package cn.fuguang.order.service;

import cn.fuguang.entity.VerifyCodeEntity;

public interface VerifyCodeService {

    /**
     * 生成验证码
     */
    String createVerifyCode();

    /**
     * 构建实体
     */
    VerifyCodeEntity buildVerifyCodeEntity(String mobile, String verifyCode, String orderNo);

    /**
     * 保存
     */
    void save(VerifyCodeEntity verifyCodeEntity);
}
