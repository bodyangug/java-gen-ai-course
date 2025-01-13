package com.epam.training.gen.ai.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;

/**
 * Service class for handling vector operations such as building embeddings, saving vectors, and searching.
 * <p>
 * This service integrates with OpenAI for generating embeddings and Qdrant for vector database operations.
 */
@Slf4j
@Service
public class SimpleVectorService {

    private final OpenAIAsyncClient openAIAsyncClient;
    private final QdrantClient qdrantClient;
    private final String embeddingModel;
    private final String collectionName;

    /**
     * Constructs a new {@code SimpleVectorService}.
     *
     * @param openAIAsyncClient the client for interacting with OpenAI APIs
     * @param qdrantClient      the client for interacting with the Qdrant vector database
     * @param embeddingModel    the embedding model name
     * @param collectionName    the name of the Qdrant collection
     */
    public SimpleVectorService(
            OpenAIAsyncClient openAIAsyncClient,
            QdrantClient qdrantClient,
            @Value("${embedding.model}") String embeddingModel,
            @Value("${embedding.collection-name}") String collectionName
    ) {
        this.openAIAsyncClient = openAIAsyncClient;
        this.qdrantClient = qdrantClient;
        this.embeddingModel = embeddingModel;
        this.collectionName = collectionName;
    }

    /**
     * Processes the input text, generates embeddings, converts them into vector points,
     * and saves them to the Qdrant collection.
     *
     * @param text the input text to be processed into embeddings
     * @throws ExecutionException   if the vector saving operation fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public void processAndSaveText(String text) throws ExecutionException, InterruptedException {
        List<String> textChunks = splitText(text, 500, 50);
        var pointStructs = new ArrayList<Points.PointStruct>();
        for (String chunk : textChunks) {
            var embeddings = getEmbeddings(chunk);

            for (var embeddingItem : embeddings) {
                var point = new ArrayList<>(embeddingItem.getEmbedding());
                pointStructs.add(getPointStruct(point, chunk));
            }
        }
        saveVector(pointStructs);
    }

    /**
     * Searches for the closest embeddings in the Qdrant collection based on the input text.
     *
     * @param text the input text for searching similar embeddings
     * @return a list of scored points representing the closest embeddings
     * @throws ExecutionException   if the search operation fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public List<Points.ScoredPoint> search(String text) throws ExecutionException, InterruptedException {
        var embeddings = retrieveEmbeddings(text);
        var qe = new ArrayList<Float>();
        embeddings.block().getData().forEach(embedding -> qe.addAll(embedding.getEmbedding()));
        return qdrantClient
                .searchAsync(
                        Points.SearchPoints.newBuilder()
                                .setCollectionName(collectionName)
                                .addAllVector(qe)
                                .setLimit(50)
                                .setWithPayload(enable(true))
                                .setScoreThreshold(0.8f)
                                .build()
                ).get();
    }

    /**
     * Retrieves embeddings for the given text using the OpenAI API.
     *
     * @param text the input text for generating embeddings
     * @return a list of embedding items
     */
    public List<EmbeddingItem> getEmbeddings(String text) {
        var embeddings = retrieveEmbeddings(text);
        return embeddings.block().getData();
    }

    /**
     * Creates a new collection in the Qdrant database with the specified parameters.
     *
     * @throws ExecutionException   if the collection creation operation fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public void createCollection() throws ExecutionException, InterruptedException {
        var result = qdrantClient.createCollectionAsync(collectionName,
                        Collections.VectorParams.newBuilder()
                                .setDistance(Collections.Distance.Cosine)
                                .setSize(1536)
                                .build())
                .get();
        log.info("Collection was created: [{}]", result.getResult());
    }

    /**
     * Saves the list of vector point structures to the Qdrant collection.
     * If the collection does not exist, it is created first.
     *
     * @param pointStructs the list of vector point structures to be saved
     * @throws ExecutionException   if the save operation fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    private void saveVector(ArrayList<Points.PointStruct> pointStructs)
            throws InterruptedException, ExecutionException {
        try {
            qdrantClient.getCollectionInfoAsync(collectionName).get();
        } catch (Exception ex) {
            log.info("Collection '{}' not found. Creating a new collection...", collectionName);
            createCollection();
        }
        var updateResult = qdrantClient.upsertAsync(collectionName, pointStructs).get();
        log.info("Upsert status: {}", updateResult.getStatus().name());
    }

    /**
     * Constructs a vector point structure from a list of float values.
     *
     * @param point the vector values
     * @return a {@link Points.PointStruct} object containing the vector and associated metadata
     */
    private Points.PointStruct getPointStruct(List<Float> point, String chunk) {
        return Points.PointStruct.newBuilder()
                .setId(id(UUID.randomUUID()))
                .setVectors(vectors(point))
                .putAllPayload(Map.of("content", value(chunk)))
                .build();
    }

    /**
     * Asynchronously retrieves embeddings for the given text using the OpenAI API.
     *
     * @param text the input text for generating embeddings
     * @return a {@link Mono} containing the embeddings
     */
    private Mono<Embeddings> retrieveEmbeddings(String text) {
        var qembeddingsOptions = new EmbeddingsOptions(List.of(text));
        return openAIAsyncClient.getEmbeddings(embeddingModel, qembeddingsOptions);
    }

    /**
     * Splits a given text into chunks of a specified size with optional overlap between chunks.
     *
     * @param text      The text to be split into chunks.
     * @param chunkSize The size of each chunk.
     * @param overlap   The number of overlapping characters between consecutive chunks.
     * @return A {@link List} of strings, where each string represents a chunk of the original text.
     */
    private List<String> splitText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            String chunk = text.substring(start, end);
            chunks.add(chunk);
            start += chunkSize - overlap;
        }
        return chunks;
    }
}
