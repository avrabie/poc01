package com.execodex.poc01.controller;

import com.execodex.poc01.model.CvData;
import com.execodex.poc01.service.AiCvParser;

import com.execodex.poc01.service.ReactivePdfParser;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@RestController
public class FileUploadController {

    private static final Path UPLOAD_DIR = Paths.get("uploads");
    private final ReactivePdfParser pdfParser;
    private final AiCvParser aiCvParser;

    public FileUploadController(ReactivePdfParser pdfParser,  AiCvParser aiCvParser) {
        this.pdfParser = pdfParser;
        this.aiCvParser = aiCvParser;
        if (!UPLOAD_DIR.toFile().exists()) {
            UPLOAD_DIR.toFile().mkdirs();
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> uploadFile(@RequestPart("file") FilePart filePart) {

        Path filePath = UPLOAD_DIR.resolve(filePart.filename());

        // Fully reactive file writing
        Mono<ResponseEntity<String>> responseEntityMono = DataBufferUtils.write(filePart.content(), filePath,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE)
                .then(Mono.just(ResponseEntity.ok("File uploaded successfully: " + filePart.filename())))
                .onErrorResume(e -> {
                    // Log error here if needed
                    return Mono.just(
                            ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage())
                    );
                });
        return responseEntityMono;
    }

    @PostMapping(value = "/parse-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> uploadAndParsePdf(@RequestPart("file") FilePart filePart) {
        Path filePath = UPLOAD_DIR.resolve(filePart.filename());

        return DataBufferUtils.write(filePart.content(), filePath,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE)
                .then(Mono.defer(() -> {
                    Mono<String> stringMono = pdfParser.parsePdf(filePath);
                    return stringMono;
                }))
                .map(text -> ResponseEntity
                        .ok("PDF parsed successfully. Text length: " + text.length() + "\n" + text))
                .onErrorResume(e -> {
                    // Clean up failed upload
                    try {
                        Files.deleteIfExists(filePath);
                    } catch (IOException ignored) {
                    }
                    return Mono.just(
                            ResponseEntity.badRequest().body("Failed to process PDF: " + e.getMessage())
                    );
                });
    }


    @PostMapping(value = "/process-cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<CvData>> processCv(@RequestPart("file") FilePart filePart) {
        Path filePath = UPLOAD_DIR.resolve(filePart.filename());

        return DataBufferUtils.write(filePart.content(), filePath,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE)
                .then(pdfParser.parsePdf(filePath))
                .flatMap(text -> aiCvParser.parseCv(text))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
                    problem.setTitle("CV Processing Error");
                    problem.setDetail(e.getMessage());
                    return Mono.just(ResponseEntity.of(problem).build());
                });

    }

    @PostMapping(value = "/process-cv-2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> processCv2(@RequestPart("file") FilePart filePart) {
        Path filePath = UPLOAD_DIR.resolve(filePart.filename());

        return DataBufferUtils.write(filePart.content(), filePath,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE)
                .then(pdfParser.parsePdf(filePath))
                .flatMap(text -> aiCvParser.parseCv2(text).singleOrEmpty())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
                    problem.setTitle("CV Processing Error");
                    problem.setDetail(e.getMessage());
                    return Mono.just(ResponseEntity.of(problem).build());
                });

    }
}