package com.execodex.poc01.service;

import com.execodex.poc01.model.CvData2;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class AiCvParser {


    @Value("classpath:prompts/cvParserPrompt.st")
    private Resource cvParserResource;

    private static final Path UPLOAD_DIR = Paths.get("uploads");
    private final ChatClient chatClient;
    private final ReactivePdfParser pdfParser;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiCvParser(ChatClient.Builder builder, ReactivePdfParser pdfParser) {
        this.chatClient = builder
                .defaultOptions(ChatOptions.builder().temperature(0.0d).build())
                .build();
        this.pdfParser = pdfParser;

        if (!UPLOAD_DIR.toFile().exists()) {
            UPLOAD_DIR.toFile().mkdirs();
        }

    }


    public Mono<String> parseCv(String cvText) throws IOException {

        Path filePath = UPLOAD_DIR.resolve("iaka.pdf");

        PDDocument document = PDDocument.load(filePath.toFile());
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(true);
        String text = stripper.getText(document);
        log.info(text);


        SystemMessage systemMessage = new SystemMessage("You are a software engineer, that takes some text as input which includes information of a person's CV. You need to parse the CV and return the parsed information. Render the CV in a JSON format");
        UserMessage userMessage = new UserMessage(text.substring(8000, 11944));


        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt(new Prompt(systemMessage, userMessage));

        ChatClient.CallResponseSpec call = requestSpec.call();
        Mono<String> tMono = Mono.defer(() -> Mono.just(call.content()))
                .subscribeOn(Schedulers.boundedElastic());
        return tMono;


    }

    public Mono<CvData2> parseCv2(String text) {


        SystemMessage systemMessage = new SystemMessage(cvParserResource);
        int size = Math.min(text.length(), 5000);
        UserMessage userMessage = new UserMessage(text.substring(0, size));

        Prompt prompt = new Prompt(systemMessage, userMessage);
//        Prompt prompt = new Prompt(systemMessage, userMessage);
        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt(prompt);

        ChatClient.CallResponseSpec call = requestSpec.call();
        Mono<String> tMono = Mono.defer(() -> Mono.just(requestSpec.call().content()))
                .subscribeOn(Schedulers.boundedElastic());
        Mono<CvData2> cvData2Mono = tMono.map(str -> str.replaceAll("```json", "")
                        .replaceAll("```", ""))
                .flatMap(str -> {
                    try {
                        return Mono.just(objectMapper.readValue(str, CvData2.class));
                    } catch (JsonProcessingException e) {
                        log.error("Failed to parse JSON: {}", str, e);
                        return Mono.error(e);
                    }
                });

        return cvData2Mono;
    }


}
