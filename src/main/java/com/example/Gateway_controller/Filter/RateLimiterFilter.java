package com.example.Gateway_controller.Filter;

import com.example.Gateway_controller.Config.RateLimitConfig;
import com.example.Gateway_controller.Service.MetricsService;
import com.example.Gateway_controller.Policy.RateLimitPolicyService;
import com.example.Gateway_controller.RedisService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RateLimiterFilter implements GatewayFilter {

    private final RedisService redisService;
    private final RateLimitPolicyService policyService;
    private final MetricsService metricsService;

    public RateLimiterFilter(
            RedisService redisService,
            RateLimitPolicyService policyService,
            MetricsService metricsService
    ) {
        this.redisService = redisService;
        this.policyService = policyService;
        this.metricsService = metricsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        // Step 1: Resolve user identity

        String userId =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst("X-User-Id");

        String clientIp =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst("X-Forwarded-For");

        String clientKey;

        if (userId != null && !userId.isEmpty()) {

            clientKey = "user:" + userId;

        } else if (clientIp != null && !clientIp.isEmpty()) {

            clientKey = "ip:" + clientIp;

        } else {

            clientKey = "anonymous";
        }

        // Step 2: Resolve route

        String path =
                exchange.getRequest()
                        .getURI()
                        .getPath();

        String routeKey = resolveRouteKey(path);

        // Step 3: Build Redis key

        String redisKey =
                buildRedisKey(clientKey, routeKey);

        // Step 4: Fetch route policy

        RateLimitConfig config =
                policyService.getPolicy(path);

        // Step 5: Increment general metrics

        metricsService.incrementTotalRequests()
                .subscribe();

        metricsService.incrementRouteHits(routeKey)
                .subscribe();

        metricsService.incrementUserHits(clientKey)
                .subscribe();

        // Step 6: Execute token bucket

        return redisService.tryConsume(
                        redisKey,
                        config.capacity(),
                        config.refillRate()
                )

                .flatMap(result -> {

                    // BLOCK REQUEST

                    if (result == 0) {

                        metricsService
                                .incrementBlockedRequests()
                                .subscribe();

                        exchange.getResponse()
                                .setStatusCode(
                                        HttpStatus.TOO_MANY_REQUESTS
                                );

                        return exchange.getResponse()
                                .setComplete();
                    }

                    // ALLOW REQUEST

                    metricsService
                            .incrementAllowedRequests()
                            .subscribe();

                    return chain.filter(exchange);
                });
    }

    @NonNull
    private String resolveRouteKey(String path) {

        if (path.startsWith("/login")) {
            return "login";
        }

        if (path.startsWith("/search")) {
            return "search";
        }

        if (path.startsWith("/admin")) {
            return "admin";
        }

        return "default";
    }

    @NonNull
    private String buildRedisKey(
            String clientKey,
            String routeKey
    ) {

        return "rate_limit:"
                + clientKey
                + ":"
                + routeKey;
    }
}