package com.execodex.poc01.controller;

import com.execodex.poc01.service.AiCvParser;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;

@RestController
public class IakaController {

    private final ChatClient.Builder chatClientBuilder;
    private final ChatClient chatClient;
    private final AiCvParser aiCvParser;

    public IakaController(ChatClient.Builder chatClientBuilder, AiCvParser aiCvParser) {
        this.chatClientBuilder = chatClientBuilder;
        chatClient = chatClientBuilder.build();
        this.aiCvParser = aiCvParser;
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

    @GetMapping("/gemini")
    public Mono<String> gemini() throws IOException {
        return aiCvParser.parseCv("I am a software engineer");
    }


}
