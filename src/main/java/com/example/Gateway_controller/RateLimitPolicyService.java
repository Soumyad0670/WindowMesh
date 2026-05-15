package com.example.Gateway_controller;

import org.springframework.stereotype.Service;

@Service
public class RateLimitPolicyService {

    public RateLimitConfig getPolicy(String path) {

        if (path.startsWith("/login")) {
            return new RateLimitConfig(5, 1);
        }

        if (path.startsWith("/search")) {
            return new RateLimitConfig(30, 5);
        }

        if (path.startsWith("/admin")) {
            return new RateLimitConfig(2, 1);
        }

        // Default policy
        return new RateLimitConfig(10, 2);
    }
}