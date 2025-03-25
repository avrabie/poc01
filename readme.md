1. File upload done. @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public Mono<ResponseEntity<String>> uploadFile(@RequestPart("file") FilePart filePart)
2. curl -X POST -F "file=@./browser.txt" http://localhost:8080/upload
2. 