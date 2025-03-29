package com.execodex.poc01.service;

import com.execodex.poc01.model.CvData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class AiCvParser {


    private static final Path UPLOAD_DIR = Paths.get("uploads");
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("classpath:prompts/cvParserPrompt.st")
    private Resource cvParserResource;

    public AiCvParser(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultOptions(ChatOptions.builder().temperature(0.0d).build())
                .build();

        if (!UPLOAD_DIR.toFile().exists()) {
            UPLOAD_DIR.toFile().mkdirs();
        }

    }




    public Mono<CvData> parseCv(String text) {


        SystemMessage systemMessage = new SystemMessage(cvParserResource);
        int size = Math.min(text.length(), 5000);
        UserMessage userMessage = new UserMessage(text.substring(0, size));

        Prompt prompt = new Prompt(systemMessage, userMessage);
        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt(prompt);
        // Call the chat client and get the response
        Mono<CvData> cvData2Mono = Mono.defer(() ->
                        Mono.just(requestSpec.call().content()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(str -> str.replaceAll("```json", "")
                        .replaceAll("```", ""))
                .flatMap(str -> {
                    try {
                        return Mono.just(objectMapper.readValue(str, CvData.class));
                    } catch (JsonProcessingException e) {
                        log.error("Failed to parse JSON: {}", str, e);
                        return Mono.error(e);
                    }
                });

        return cvData2Mono;
    }


}
