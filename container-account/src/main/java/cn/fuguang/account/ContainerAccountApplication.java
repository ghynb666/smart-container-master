package cn.fuguang.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"cn.fuguang.api"})
@MapperScan("cn.fuguang.account.mapper")
public class ContainerAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContainerAccountApplication.class);
    }
}
