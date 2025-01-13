package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.domain.EmbeddingModelResponse;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Service for managing context data in a Retrieval-Augmented Generation (RAG) system.
 * Provides functionality to upload, process, and search context data.
 */
@Service
public class RagService {

    private final SimpleVectorService simpleVectorService;

    /**
     * Constructor to initialize the RagService with required dependencies.
     *
     * @param simpleVectorService The service responsible for vectorizing and storing text data.
     */
    @Autowired
    public RagService(SimpleVectorService simpleVectorService) {
        this.simpleVectorService = simpleVectorService;
    }

    /**
     * Saves the uploaded file to a directory, extracts its text content, and processes it for vector storage.
     *
     * @param file The uploaded file containing context information.
     * @throws IOException          If an error occurs during file handling or text extraction.
     * @throws ExecutionException   If an error occurs during text processing.
     * @throws InterruptedException If the thread is interrupted during text processing.
     */
    public void saveContext(MultipartFile file) throws IOException, ExecutionException, InterruptedException {
        File uploadedFile = saveToDir(file);
        String content = extractTextFromFile(uploadedFile);
        simpleVectorService.processAndSaveText(content);
    }

    /**
     * Searches the context using a query and retrieves matching results as a list of DTOs.
     *
     * @param query The search query to find relevant context.
     * @return A list of {@link EmbeddingModelResponse} containing the search results.
     * @throws ExecutionException   If an error occurs during the search process.
     * @throws InterruptedException If the thread is interrupted during the search process.
     */
    public List<EmbeddingModelResponse> searchContext(String query) throws ExecutionException, InterruptedException {
        var results = simpleVectorService.search(query);

        return results.stream()
                .map(EmbeddingModelResponse::toDTO)
                .toList();
    }

    /**
     * Extracts text content from a PDF file.
     *
     * @param file The PDF file to extract text from.
     * @return The extracted text content as a string.
     * @throws IOException If an error occurs during text extraction.
     */
    private String extractTextFromFile(File file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Saves the uploaded file to the designated directory.
     *
     * @param content The uploaded file to be saved.
     * @return A {@link File} object representing the saved file.
     */
    private File saveToDir(MultipartFile content) {
        Path uploadDir = getOrCreateDir();
        Path targetLocation = uploadDir.resolve(Objects.requireNonNull(content.getOriginalFilename()));
        try {
            Files.copy(content.getInputStream(), targetLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetLocation.toFile();
    }

    /**
     * Ensures the existence of the upload directory. Creates it if it does not exist.
     *
     * @return The path to the upload directory.
     */
    private Path getOrCreateDir() {
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uploadDir;
    }
}
