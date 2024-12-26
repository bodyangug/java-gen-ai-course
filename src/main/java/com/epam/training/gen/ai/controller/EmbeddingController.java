package com.epam.training.gen.ai.controller;

import com.azure.ai.openai.models.EmbeddingItem;
import com.epam.training.gen.ai.domain.EmbeddingModelRequest;
import com.epam.training.gen.ai.domain.EmbeddingModelResponse;
import com.epam.training.gen.ai.service.SimpleVectorService;
import io.qdrant.client.grpc.Points;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * REST controller for managing embedding-related operations.
 * <p>
 * Provides endpoints to build embeddings from text, store them in a vector database,
 * and search for the closest embeddings based on input text.
 */
@RestController
@RequestMapping("/embeddings")
public class EmbeddingController {

    private final SimpleVectorService vectorService;

    /**
     * Constructor for EmbeddingController.
     *
     * @param vectorService the service responsible for embedding operations
     */
    public EmbeddingController(SimpleVectorService vectorService) {
        this.vectorService = vectorService;
    }

    /**
     * Endpoint to build embeddings from the provided text.
     *
     * @param model the request containing the input text for building embeddings
     * @return a ResponseEntity containing the list of generated embeddings or an error status
     */
    @PostMapping("/build")
    public ResponseEntity<List<EmbeddingItem>> buildEmbedding(@RequestBody EmbeddingModelRequest model) {
        try {
            List<EmbeddingItem> embeddings = vectorService.getEmbeddings(model.getText());
            return ResponseEntity.ok(embeddings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint to build embeddings from the provided text and store them in the vector database.
     *
     * @param model the request containing the input text for building and storing embeddings
     * @return a ResponseEntity containing a success message or an error status
     */
    @PostMapping("/build-and-store")
    public ResponseEntity<String> buildAndStoreEmbedding(@RequestBody EmbeddingModelRequest model) {
        try {
            vectorService.processAndSaveText(model.getText());
            return ResponseEntity.ok("Embeddings successfully built and stored.");
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process and save embeddings.");
        }
    }

    /**
     * Endpoint to search for the closest embeddings based on the provided text.
     *
     * @param model the request containing the input text for searching closest embeddings
     * @return a ResponseEntity containing a list of matching embeddings or an error status
     */
    @PostMapping("/search")
    public ResponseEntity<List<EmbeddingModelResponse>> searchClosestEmbeddings(
            @RequestBody EmbeddingModelRequest model) {
        try {
            List<Points.ScoredPoint> results = vectorService.search(model.getText());
            var t = results.stream()
                    .map(EmbeddingModelResponse::toDTO)
                    .toList();
            return ResponseEntity.ok(t);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

