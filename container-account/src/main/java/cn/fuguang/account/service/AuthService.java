package cn.fuguang.account.service;

import cn.fuguang.account.pojo.vo.req.LoginReq;
import cn.fuguang.account.pojo.vo.req.RegisterReq;
import cn.fuguang.account.pojo.vo.res.LoginRes;

public interface AuthService {

    /**
     * 登录
     * @param req
     * @return
     */
    LoginRes login(LoginReq req);

    /**
     * 注册
     * @param req
     */
    void register(RegisterReq req);
}
