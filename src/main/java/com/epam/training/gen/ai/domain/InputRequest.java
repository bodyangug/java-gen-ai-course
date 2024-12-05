package com.epam.training.gen.ai.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class InputRequest implements Serializable {
    @JsonProperty("user-id")
    private String userId;
    private String input;
    @JsonProperty("model-id")
    private String modelId;
}
