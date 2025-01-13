package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.domain.InputRequest;
import com.epam.training.gen.ai.service.ChatService;
import com.epam.training.gen.ai.service.RagService;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for handling operations related to Retrieval-Augmented Generation (RAG).
 * Provides endpoints for uploading context and answering queries based on the context.
 */
@RestController
@RequestMapping("/rag")
public class RagController {
    private final RagService ragService;
    private final ChatService chatService;
    private final Kernel kernel;

    /**
     * Constructor to initialize the RagController with required services.
     *
     * @param ragService  The service responsible for handling context storage and retrieval.
     * @param chatService The service responsible for interacting with the chat system.
     */
    @Autowired
    public RagController(RagService ragService, ChatService chatService, Kernel kernel) {
        this.ragService = ragService;
        this.chatService = chatService;
        this.kernel = kernel;
    }

    /**
     * Endpoint to upload context data.
     * Accepts a file containing context information and saves it using the RAG service.
     *
     * @param content The file containing the context to be uploaded.
     * @return A ResponseEntity with a success or error message.
     */
    @PostMapping("/upload-context")
    public ResponseEntity<String> uploadContext(@RequestParam MultipartFile content) {
        try {
            ragService.saveContext(content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Context uploaded successfully.");
    }

    /**
     * Endpoint to search the context and answer a query.
     * Generates a response based on the context and the question provided in the request.
     *
     * @param request The input request containing the question, input text, and model ID.
     * @return A ResponseEntity containing the result or an error message.
     */
    @PostMapping("/answer-by-context")
    public ResponseEntity<String> searchContext(@RequestBody InputRequest request) {
        try {
            String context = kernel.invokeAsync("ContentRetrieverPlugin", "ContentRetriever")
                    .withArguments(
                            KernelFunctionArguments.builder()
                                    .withVariable("userQuestion", request.getInput())
                                    .build()
                    )
                    .withResultType(String.class).block().getResult();

            String prompt = String.format(
                    "Use the information that you get from the context and answer the question." +
                            "If you don't have context, just say that you don't know, don't try to make up an answer." +
                            "\nQuestion: %s" +
                            "\nContext: %s",
                    request.getInput(),
                    context
            );
            String result = chatService.ask(request.getUserId(), prompt, request.getModelId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
