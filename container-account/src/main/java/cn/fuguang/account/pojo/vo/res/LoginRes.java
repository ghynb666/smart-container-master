package cn.fuguang.account.pojo.vo.res;

import cn.fuguang.entity.CustomerEntity;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRes {
    private String token;
    private CustomerEntity userInfo;
}
