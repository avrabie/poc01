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

//        Path filePath = UPLOAD_DIR.resolve("iaka.pdf");

//        PDDocument document = null;
//        String textCV = null;
//        try {
//            document = PDDocument.load(filePath.toFile());
//            PDFTextStripper stripper = new PDFTextStripper();
//            stripper.setSortByPosition(true);
//            textCV = stripper.getText(document);
//            log.info(textCV);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        String systemMessageStr = """
                You are a professional CV parser. Transform raw txt CV information into JSON, try to fill the information where possible, matching this EXACT structure:
                
                {
                  "personal_information": {
                    "name": string,
                    "title": string,
                    "phone_numbers": string[],
                    "email": string,
                    "social_media": {
                      "stackoverflow": string,
                      "github": string
                    }
                  },
                  "skills": {
                    "programming_languages": string[],
                    "java_frameworks": string[],
                    "technologies": string[],
                    "package_management": string[],
                    "version_control": string[],
                    "databases": string[],
                    "containerization": string[],
                    "orchestration": string[]
                  },
                  "summary": string[],
                  "experience": [{
                    "company": string,
                    "start_date": string,
                    "end_date": string,
                    "client": string,
                    "role": string,
                    "description": string[]
                  }],
                  "education": [{
                    "institution": string,
                    "degree": string,
                    "thesis": string,
                    "dates": string,
                    "concentration": string
                  }],
                  "publications": [{
                    "title": string,
                    "journal": string,
                    "pages": string,
                    "isbn": string,
                    "location": string,
                    "year": string
                  }]
                }
                
                Follow these strict rules:
                1. Use EXACT field names and hierarchy shown above
                2. Format phone numbers with country codes in parentheses
                3. Maintain array order from original CV chronology
                4. Preserve exact technical terms (e.g., "H2 (dev)", "Mongo DB")
                5. Experience dates use "MMM, YYYY" format (e.g., "Jun, 2022")
                6. Education entries must include thesis/concentration when present
                7. Publications require full ISBN and journal details
                8. Return ONLY valid JSON (no Markdown, comments, or text)
                9. Include ALL sections even if empty arrays
                
                Example of valid structure:
                {
                  "personal_information": {
                    "social_media": {"stackoverflow": "..."},
                    ...
                  },
                  "skills": {
                    "programming_languages": ["Java", ...],
                    ...
                  }
                  // ... other sections
                }
                
                Invalid responses will cause application errors. Double-check:
                - All commas and brackets
                - No trailing commas
                - Exact field spelling
                - Consistent quotation marks
                """;
        SystemMessage systemMessage = new SystemMessage(systemMessageStr);
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
