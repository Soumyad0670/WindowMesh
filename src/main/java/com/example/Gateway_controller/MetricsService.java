package com.example.Gateway_controller;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MetricsService {

    private final ReactiveStringRedisTemplate redisTemplate;

    public MetricsService(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // TOTAL REQUESTS

    public Mono<Long> incrementTotalRequests() {

        return redisTemplate.opsForValue()
                .increment("metrics:total_requests");
    }

    // ALLOWED REQUESTS

    public Mono<Long> incrementAllowedRequests() {

        return redisTemplate.opsForValue()
                .increment("metrics:allowed_requests");
    }

    // BLOCKED REQUESTS

    public Mono<Long> incrementBlockedRequests() {

        return redisTemplate.opsForValue()
                .increment("metrics:blocked_requests");
    }

    // ROUTE METRICS

    public Mono<Long> incrementRouteHits(String route) {

        return redisTemplate.opsForValue()
                .increment("metrics:route:" + route);
    }

    // USER METRICS

    public Mono<Long> incrementUserHits(String user) {

        return redisTemplate.opsForValue()
                .increment("metrics:user:" + user);
    }
}