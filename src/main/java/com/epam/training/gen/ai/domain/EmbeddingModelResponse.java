package com.epam.training.gen.ai.domain;

import io.qdrant.client.grpc.Points;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A data transfer object (DTO) representing a response for embedding-related operations.
 * <p>
 * This class encapsulates the ID and score of a vector point returned from search operations.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EmbeddingModelResponse {

    /**
     * The unique identifier of the embedding vector.
     */
    private Long id;

    /**
     * The similarity score of the embedding vector.
     */
    private Float score;

    /**
     * Converts a {@link Points.ScoredPoint} object into an {@link EmbeddingModelResponse} DTO.
     *
     * @param scoredPoint the scored point retrieved from the vector database
     * @return an instance of {@link EmbeddingModelResponse} containing the ID and score of the scored point
     */
    public static EmbeddingModelResponse toDTO(Points.ScoredPoint scoredPoint) {
        return new EmbeddingModelResponse(
                scoredPoint.getId().getNum(),
                scoredPoint.getScore()
        );
    }
}
