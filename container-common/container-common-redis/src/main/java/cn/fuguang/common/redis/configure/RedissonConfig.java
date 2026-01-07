package cn.fuguang.common.redis.configure;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    String redisHost;

    @Value("${spring.redis.port:6379}")
    String redisPort;


    @Bean
    public RedissonClient redissonClient() {
        //1、创建配置
        Config config = new Config();

        redisHost = redisHost.startsWith("redis://") ? redisHost : "redis://" + redisHost;
        config.useSingleServer()
                .setAddress(redisHost + ":" + redisPort);

        return Redisson.create(config);
    }
}
