1. File upload done. @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public Mono<ResponseEntity<String>> uploadFile(@RequestPart("file") FilePart filePart)
2. curl -X POST -F "file=@./browser.txt" http://localhost:8080/upload
3. Parsing PDF file using PDFBox
4. Adding AI https://aistudio.google.com/apikey from Gemini (because it has some free tokens)
5. transformed the parsed pdf into a CvData
6. getting repository data from github https://api.github.com/users/avrabie/repos?type=owner&sort=updated&direction=desc&per_page=5&page=1
7. Added OpenAI API key https://platform.openai.com/api-keys
