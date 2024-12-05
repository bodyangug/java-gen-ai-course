package com.epam.training.gen.ai.configuration;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration class for setting up the OpenAI client.
 * This class defines the configuration required to connect to the Azure OpenAI service.
 */
@Configuration
public class OpenAIConfiguration {

    /**
     * Creates and configures an asynchronous OpenAI client.
     *
     * @param apiKey   the API key used for authenticating with the Azure OpenAI service
     * @param endpoint the endpoint URL of the Azure OpenAI service
     * @return an instance of {@link OpenAIAsyncClient}
     */
    @Bean
    public OpenAIAsyncClient openAIAsyncClient(
            @Value("${client-azureopenai-key}") String apiKey,
            @Value("${client-azureopenai-endpoint}") String endpoint
    ) {
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(endpoint)
                .buildAsyncClient();
    }
}