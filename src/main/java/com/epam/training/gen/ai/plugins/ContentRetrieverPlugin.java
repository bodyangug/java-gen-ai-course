package com.epam.training.gen.ai.plugins;

import com.epam.training.gen.ai.domain.EmbeddingModelResponse;
import com.epam.training.gen.ai.service.RagService;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Plugin class for retrieving content based on a user's query.
 * It uses the RagService to search and return the relevant context.
 */
@Slf4j
@Component
public class ContentRetrieverPlugin {

    private final RagService ragService;

    /**
     * Constructor to initialize the ContentRetrieverPlugin with a RagService instance.
     *
     * @param ragService The RagService instance used for context retrieval.
     */
    @Autowired
    public ContentRetrieverPlugin(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * Kernel function to retrieve context based on the user's query.
     *
     * @param userQuestion The query provided by the user to retrieve relevant context.
     * @return The retrieved context as a string. If no context is found, a default response is returned.
     * In case of an error, an appropriate error message is returned.
     */
    @DefineKernelFunction(name = "ContentRetriever", description = "Retrieves context based on the user question.")
    public String retrieveContext(
            @KernelFunctionParameter(description = "Question from user", name = "userQuestion")
            String userQuestion
    ) {
        try {
            List<EmbeddingModelResponse> context = ragService.searchContext(userQuestion);
            String collect = context.stream().map(EmbeddingModelResponse::getChunk).collect(Collectors.joining());
            if (collect.isBlank()) {
                return "I don't have context based on the user query";
            }
            return collect;
        } catch (Exception ex) {
            return "Sorry, I cannot retrieve context based on the user query";
        }
    }
}
