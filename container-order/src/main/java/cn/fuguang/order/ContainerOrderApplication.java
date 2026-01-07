package cn.fuguang.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"cn.fuguang.api"})
@MapperScan("cn.fuguang.order.mapper")
public class ContainerOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContainerOrderApplication.class);
    }
}
