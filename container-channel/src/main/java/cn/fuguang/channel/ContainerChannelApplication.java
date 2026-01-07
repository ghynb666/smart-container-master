package cn.fuguang.channel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties
@MapperScan("cn.fuguang.channel.mapper")
public class ContainerChannelApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContainerChannelApplication.class);
    }
}
