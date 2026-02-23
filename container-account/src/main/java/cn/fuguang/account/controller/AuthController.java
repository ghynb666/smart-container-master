package cn.fuguang.account.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.fuguang.account.mapper.CustomerMapper;
import cn.fuguang.account.pojo.vo.req.LoginReq;
import cn.fuguang.account.pojo.vo.req.RegisterReq;
import cn.fuguang.account.pojo.vo.res.LoginRes;
import cn.fuguang.account.service.AuthService;
import cn.fuguang.entity.CustomerEntity;
import cn.fuguang.web.BaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerMapper customerMapper;

    @PostMapping("/login")
    public BaseResult<LoginRes> login(@RequestBody LoginReq req) {
        return BaseResult.success(authService.login(req));
    }

    @PostMapping("/register")
    public BaseResult<Void> register(@RequestBody RegisterReq req) {
        authService.register(req);
        return BaseResult.success();
    }

    @PostMapping("/logout")
    public BaseResult<Void> logout() {
        StpUtil.logout();
        return BaseResult.success();
    }

    @GetMapping("/info")
    public BaseResult<CustomerEntity> info() {
        String loginId = (String) StpUtil.getLoginId();
        return BaseResult.success(customerMapper.selectById(loginId));
    }
}
