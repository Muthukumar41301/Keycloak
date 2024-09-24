package com.keycloak.auth.config.redis;

import io.lettuce.core.resource.ClientResources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

/*
* This configuration is used in Spring-based applications that require Redis caching or data storage.
* Spring Data Redis would internally use this connection factory to perform operations like caching or session management.
*/
@Configuration
public class RedisClientConnection {

    @Value(value = "${redis.config.cache.host}")
    private String host;
    @Value(value = "${redis.config.cache.port}")
    private int port;
    @Value(value = "${redis.config.cache.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        var redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        return new LettuceConnectionFactory(
                redisStandaloneConfiguration,
                LettuceClientConfiguration.builder()
                        .commandTimeout(Duration.ofSeconds(30))
                        .clientResources(ClientResources.builder().build())
                        .build()
        );
    }
}
