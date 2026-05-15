package com.example.Gateway_controller.Service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RedisService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisScript<Long> script;

    public RedisService(ReactiveStringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;

        this.script = RedisScript.of(
                new ClassPathResource("rate_limiter.lua"),
                Long.class
        );
    }

    public Mono<Long> tryConsume(
            String key,
            int capacity,
            int refillRate
    ) {

        long now = System.currentTimeMillis() / 1000;

        return redisTemplate.execute(
                script
        ).next();
    }
}