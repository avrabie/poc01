package com.execodex.poc01.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AiJobParserService {
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiJobParserService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultOptions(ChatOptions.builder().temperature(0.0d).build())
                .build();
    }
    public Flux<String> parseJob(String jobDescription) {
        String prompt = "Parse the following job description and extract the relevant information: " + jobDescription;
        Flux<String> content = chatClient.prompt().user(prompt)
                .stream()
                .content();
        return content;
    }
}
