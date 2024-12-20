package com.epam.training.gen.ai.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * A data transfer object (DTO) representing a request for embedding-related operations.
 * <p>
 * This class encapsulates the input text used for building, storing, or searching embeddings.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EmbeddingModelRequest implements Serializable {

    /**
     * The input text to be processed for embedding operations.
     */
    private String text;
}
