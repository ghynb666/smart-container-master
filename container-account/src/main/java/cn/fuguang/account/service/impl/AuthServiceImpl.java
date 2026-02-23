package cn.fuguang.account.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.fuguang.account.mapper.CustomerMapper;
import cn.fuguang.account.pojo.vo.req.LoginReq;
import cn.fuguang.account.pojo.vo.req.RegisterReq;
import cn.fuguang.account.pojo.vo.res.LoginRes;
import cn.fuguang.account.service.AuthService;
import cn.fuguang.entity.CustomerEntity;
import cn.fuguang.exception.ContainerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public LoginRes login(LoginReq req) {
        CustomerEntity customer = customerMapper.selectByUsername(req.getUsername());
        if (customer == null) {
            throw new ContainerException("100016", "用户不存在");
        }
        if (!SaSecureUtil.sha256(req.getPassword()).equals(customer.getPassword())) {
            throw new ContainerException("100016", "用户名或密码错误");
        }

        // Login
        StpUtil.login(customer.getId());

        return LoginRes.builder()
                .token(StpUtil.getTokenInfo().tokenValue)
                .userInfo(customer)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterReq req) {
        CustomerEntity exist = customerMapper.selectByUsername(req.getUsername());
        if (exist != null) {
            throw new ContainerException("100016", "用户已存在");
        }

        CustomerEntity customer = new CustomerEntity();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        customer.setCustomerId(uuid);
        customer.setUserName(req.getUsername());
        customer.setPassword(SaSecureUtil.sha256(req.getPassword()));
        customer.setMobile(req.getMobile());
        customer.setStatus("1"); // 1: Active
        customer.setCreateTime(new Date());
        customer.setUpdateTime(new Date());

        customerMapper.insert(customer);
    }
}
