package cn.fuguang.account.pojo.vo.req;

import lombok.Data;

@Data
public class RegisterReq {
    private String username;
    private String password;
    private String mobile;
}
