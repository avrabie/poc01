package com.execodex.poc01.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;

@Service
@Slf4j
public class ReactivePdfParser {

    public Mono<String> parsePdf(Path filePath) {


        return Mono.fromCallable(() -> {
                    try (PDDocument document = PDDocument.load(filePath.toFile())) {
                        PDFTextStripper stripper = new PDFTextStripper();
                        stripper.setSortByPosition(true);
                        String text = stripper.getText(document);
                        log.info(text);
                        return text;
                    }
                })
                .subscribeOn(Schedulers.boundedElastic()); // Offload blocking IO
    }
}
