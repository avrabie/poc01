1. File upload done. @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public Mono<ResponseEntity<String>> uploadFile(@RequestPart("file") FilePart filePart)
2. curl -X POST -F "file=@./browser.txt" http://localhost:8080/upload
3. Parsing PDF file using PDFBox
4. Adding AI https://aistudio.google.com/apikey from Gemini (because it has some free tokens)
