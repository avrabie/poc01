package com.execodex.poc01.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class IakaController {
    @GetMapping("/iaka")
    public Mono<String> iaka() {
        return Mono.just("Iaka");
    }
}
