package com.epam.training.gen.ai.configuration;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SemanticKernelConfiguration {

    @Bean
    public ChatCompletionService chatCompletionService(
            @Value("${client-azureopenai-deployment-name}") String deploymentOrModelName,
            OpenAIAsyncClient openAIAsyncClient) {
        return OpenAIChatCompletion.builder()
                .withModelId(deploymentOrModelName)
                .withOpenAIAsyncClient(openAIAsyncClient)
                .build();
    }

    @Bean
    public Kernel kernel(ChatCompletionService chatCompletionService) {
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();
    }

    @Bean
    public InvocationContext invocationContext() {
        return InvocationContext.builder()
                .withPromptExecutionSettings(
                        PromptExecutionSettings.builder()
                                .withTemperature(1.0)
                                .build())
                .build();
    }
}
