package com.execodex.poc01.service;

import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.ILoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class AiCvParser {


    private static final Path UPLOAD_DIR = Paths.get("uploads");
    private final ChatClient chatClient;
    private final ReactivePdfParser pdfParser;

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
        UserMessage userMessage = new UserMessage(text.substring(8000,11944));


        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt(new Prompt(systemMessage, userMessage));

        ChatClient.CallResponseSpec call = requestSpec.call();
        Mono<String> tMono = Mono.defer(() -> Mono.just(call.content()))
                .subscribeOn(Schedulers.boundedElastic());
        return tMono;


    }
}
