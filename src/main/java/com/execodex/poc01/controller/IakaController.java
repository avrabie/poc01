package com.execodex.poc01.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class IakaController {

    private final ChatClient.Builder chatClientBuilder;
    private final ChatClient chatClient;

    public IakaController(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
        chatClient = chatClientBuilder.build();
    }

    @GetMapping("/iaka")
    public Mono<String> iaka() {
        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt("Tell me a software engineering joke");

        Mono<String> stringMono = Mono.fromCallable(() -> {
            ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt("Tell me a software engineering joke");
            ChatClient.CallResponseSpec call = requestSpec.call();
            return call.content();
        }).subscribeOn(Schedulers.boundedElastic());
        return stringMono;
    }

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello World!");
    }
}
