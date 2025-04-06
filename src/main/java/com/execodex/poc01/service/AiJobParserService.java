package com.execodex.poc01.service;

import com.execodex.poc01.model.CvData;
import com.execodex.poc01.model.JobDescription;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AiJobParserService {
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("classpath:prompts/jobParserPrompt.st")
    private Resource jobParserPrompt;

    public AiJobParserService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultOptions(ChatOptions.builder().temperature(0.0d).build())
                .build();
    }
    public Flux<String> parseJob(String jobDescription) {
        String userText = """
                You are a professional job requirements parser. Transform raw text job descriptions into JSON, try to fill the information where possible, matching this EXACT structure: 
                Render the message in JSON format.
                Follow the format strictly and do not add any extra information.
                {jobDescription}
                {format}
                """;


        BeanOutputConverter<JobDescription> converter = new BeanOutputConverter<>(new ParameterizedTypeReference<JobDescription>() {
        });
        Flux<String> content = chatClient.prompt()
                .user(promptUserSpec -> {
                    promptUserSpec
                            .text(userText);
                    promptUserSpec.param("jobDescription", jobDescription);
                    promptUserSpec.param("format", converter.getFormat());
                })
                .stream()
                .content();
        return content;
    }
}
