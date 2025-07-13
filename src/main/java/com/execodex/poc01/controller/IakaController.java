package com.execodex.poc01.controller;

import com.execodex.poc01.model.CvData;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
public class IakaController {

    private final ChatClient chatClient;
    @Value("classpath:prompts/cvParserPrompt.st")
    private Resource cvParserResource;

    public IakaController(ChatClient.Builder chatClientBuilder) {
        chatClient = chatClientBuilder
                .defaultOptions(ChatOptions.builder()
                        .temperature(1.5d)
                        .build())
                .build();
    }

    @GetMapping("/github/{username}")
    public Mono<String> github(@PathVariable String username) {
        WebClient webClient = WebClient.create("https://api.github.com");

        Mono<String> stringMono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users/{username}/repos")
                        .queryParam("type", "owner")
                        .queryParam("sort", "updated")
                        .queryParam("direction", "desc")
                        .queryParam("per_page", 2)
                        .queryParam("page", 1)
                        .build(username))

                .header("Accept", "application/vnd.github.v3+json")

//                .uri("users/avrabie/repos?type=owner&sort=updated&direction=desc&per_page=2&page=1")
                .retrieve()
                .bodyToMono(String.class);
        return stringMono;
    }

    @GetMapping("/ai")
    Mono<String> generation() {

        return Mono.defer(() -> {

            String hi = this.chatClient.prompt()
                    .user("tell me a joke about software engineering")
                    .call()
                    .content();
            return Mono.just(hi);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/ai2")
    Flux<String> generation2() {
        return
                this.chatClient.prompt()
                        .user("hi")
                        .stream()
                        .content();
    }

    @GetMapping(value = "/ai/client")
    Flux<String> generation2(@RequestBody String inquiry) {
        return
                this.chatClient.prompt()
                        .user(inquiry)
                        .stream()
                        .content();
    }

    @GetMapping("/ai3")
    Mono<CvData> generation3() {
        return
                Mono.defer(() -> {
                    CvData entity = this
                            .chatClient
                            .prompt()
                            .user("Generte a CV for a software engineer")
                            .call()
                            .entity(CvData.class);
                    return Mono.just(entity);
                }).subscribeOn(Schedulers.boundedElastic());

    }

    @GetMapping("/ai4")
    Flux<String> generation4() {
        BeanOutputConverter<CvData> converter = new BeanOutputConverter<>(new ParameterizedTypeReference<CvData>() {
        });
        Flux<String> chatResponseFlux = this
                .chatClient
                .prompt()
//                .system(cvParserResource)
                .user(promptUserSpec -> {
                    promptUserSpec.text("""
                            Generate a CV for a software engineer, not more than 100 words
                            {format}
                            """);
                    promptUserSpec.param("format", converter.getFormat());
                })
                .stream()
                .content();
        return chatResponseFlux;

    }

    @GetMapping("/joke")
    public Mono<String> joke() {

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
