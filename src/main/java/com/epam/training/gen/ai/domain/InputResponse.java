package com.epam.training.gen.ai.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Represents the response for a chat request.
 * This class contains the data generated as a result of processing the input request.
 * It is designed to be serialized and deserialized for communication between the server and the client.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class InputResponse implements Serializable {

    /**
     * The response data generated by the chat service.
     * Typically, this is the AI-generated response to the user's input.
     */
    private String data;
}

