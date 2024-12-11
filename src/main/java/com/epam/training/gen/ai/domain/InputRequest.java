package com.epam.training.gen.ai.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Represents the input request for the chat functionality.
 * This class contains details about the user, input message, and the model to be used for generating a response.
 * It is designed to be serialized and deserialized for communication between the client and the server.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class InputRequest implements Serializable {

    /**
     * The unique identifier of the user sending the request.
     * This field is serialized and deserialized as "user-id" in JSON.
     */
    @JsonProperty("user-id")
    private String userId;

    /**
     * The input message or query provided by the user.
     */
    private String input;

    /**
     * The identifier of the AI model to be used for generating a response.
     * This field is serialized and deserialized as "model-id" in JSON.
     */
    @JsonProperty("model-id")
    private String modelId;
}
