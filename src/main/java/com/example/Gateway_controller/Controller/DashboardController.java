package com.example.Gateway_controller.Controller;

import com.example.Gateway_controller.dto.DashboardMetrics;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class DashboardController {

    private final ReactiveStringRedisTemplate redisTemplate;

    public DashboardController(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/dashboard")
    public Mono<DashboardMetrics> getDashboard() {

        Mono<String> total =
                redisTemplate.opsForValue()
                        .get("metrics:total_requests")
                        .defaultIfEmpty("0");

        Mono<String> allowed =
                redisTemplate.opsForValue()
                        .get("metrics:allowed_requests")
                        .defaultIfEmpty("0");

        Mono<String> blocked =
                redisTemplate.opsForValue()
                        .get("metrics:blocked_requests")
                        .defaultIfEmpty("0");

        return Mono.zip(total, allowed, blocked)

                .map(tuple -> new DashboardMetrics(
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3()
                ));
    }
}