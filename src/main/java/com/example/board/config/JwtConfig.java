package com.example.board.config;

import com.example.board.component.JwtAuthenticationFilter;
import com.example.board.component.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class JwtConfig {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    @Bean
    public JwtTokenProvider jwtTokenProvider(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        return new JwtTokenProvider(redisTemplate, secretKey, accessTokenValidity, refreshTokenValidity);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            @Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate
    ) {
        return new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate);
    }
}