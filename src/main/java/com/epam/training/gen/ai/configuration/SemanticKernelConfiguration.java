package com.epam.training.gen.ai.configuration;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.epam.training.gen.ai.plugins.AgeCalculatorPlugin;
import com.epam.training.gen.ai.plugins.ContentRetrieverPlugin;
import com.epam.training.gen.ai.plugins.CurrencyConverterPlugin;
import com.epam.training.gen.ai.plugins.WeatherForecastPlugin;
import com.epam.training.gen.ai.service.RagService;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.contextvariables.ContextVariableTypes;
import com.microsoft.semantickernel.contextvariables.converters.ContextVariableJacksonConverter;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * Configuration class for setting up Semantic Kernel-related components.
 * This class defines beans for chat completion services, the kernel, and the invocation context.
 */
@Configuration
public class SemanticKernelConfiguration {

    /**
     * Creates a default {@link ChatCompletionService} using the specified deployment or model name
     * and an instance of {@link OpenAIAsyncClient}.
     *
     * @param deploymentOrModelName the deployment or model name for the default chat completion service
     * @param openAIAsyncClient     the OpenAI asynchronous client
     * @return an instance of {@link ChatCompletionService} configured with the default model
     */
    @Bean
    public ChatCompletionService defaultChatCompletionService(
            @Value("${client-azureopenai-deployments.default}") String deploymentOrModelName,
            OpenAIAsyncClient openAIAsyncClient) {
        return OpenAIChatCompletion.builder()
                .withModelId(deploymentOrModelName)
                .withOpenAIAsyncClient(openAIAsyncClient)
                .build();
    }

    /**
     * Creates a custom {@link ChatCompletionService} using the specified deployment or model name
     * and an instance of {@link OpenAIAsyncClient}.
     *
     * @param deploymentOrModelName the deployment or model name for the custom chat completion service
     * @param openAIAsyncClient     the OpenAI asynchronous client
     * @return an instance of {@link ChatCompletionService} configured with the custom model
     */
    @Bean
    public ChatCompletionService customChatCompletionService(
            @Value("${client-azureopenai-deployments.custom}") String deploymentOrModelName,
            OpenAIAsyncClient openAIAsyncClient) {
        return OpenAIChatCompletion.builder()
                .withModelId(deploymentOrModelName)
                .withOpenAIAsyncClient(openAIAsyncClient)
                .build();
    }

    /**
     * Creates a KernelPlugin bean for the age calculator functionality.
     *
     * @return a KernelPlugin instance for age calculation.
     */
    @Bean
    public KernelPlugin ageCalculatorPlugin() {
        return KernelPluginFactory.createFromObject(new AgeCalculatorPlugin(), "AgeCalculatorPlugin");
    }

    /**
     * Creates a KernelPlugin bean for the weather forecast functionality.
     *
     * @return a KernelPlugin instance for weather forecasting.
     */
    @Bean
    public KernelPlugin weatherForecastPlugin() {
        return KernelPluginFactory.createFromObject(new WeatherForecastPlugin(), "WeatherForecastPlugin");
    }

    /**
     * Creates a KernelPlugin bean for the currency converter functionality.
     *
     * @return a KernelPlugin instance for currency conversion.
     */
    @Bean
    public KernelPlugin currencyConverterPlugin() {
        return KernelPluginFactory.createFromObject(new CurrencyConverterPlugin(), "CurrencyConverterPlugin");
    }

    /**
     * Creates and registers a {@link KernelPlugin} bean for the ContentRetrieverPlugin.
     * This plugin allows the system to retrieve context based on user queries using the RAG service.
     *
     * @param ragService The {@link RagService} instance used for retrieving context data.
     * @return A {@link KernelPlugin} instance wrapping the ContentRetrieverPlugin.
     */
    @Bean
    public KernelPlugin contextRetrieverPlugin(RagService ragService) {
        return KernelPluginFactory.createFromObject(new ContentRetrieverPlugin(ragService), "ContentRetrieverPlugin");
    }

    /**
     * Configures the {@link Kernel} instance with the provided list of {@link ChatCompletionService}.
     * The kernel is responsible for managing AI services and interactions.
     *
     * @param chatCompletionServices a list of chat completion services; the first service is used by default
     * @return an instance of {@link Kernel} configured with the chat completion service
     */
    @Bean
    public Kernel kernel(List<ChatCompletionService> chatCompletionServices,
                         KernelPlugin ageCalculatorPlugin,
                         KernelPlugin weatherForecastPlugin,
                         KernelPlugin currencyConverterPlugin,
                         KernelPlugin contextRetrieverPlugin) {
        ContextVariableTypes.addGlobalConverter(ContextVariableJacksonConverter.create(CurrencyConverterPlugin.class));

        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionServices.get(0))
                .withPlugin(ageCalculatorPlugin)
                .withPlugin(weatherForecastPlugin)
                .withPlugin(currencyConverterPlugin)
                .withPlugin(contextRetrieverPlugin)
                .build();
    }

    /**
     * Creates an {@link InvocationContext} for managing execution settings for prompts.
     * The context includes settings like temperature and top-p sampling for controlling AI outputs.
     *
     * @return an instance of {@link InvocationContext} configured with default execution settings
     */
    @Bean
    public InvocationContext invocationContext() {
        return InvocationContext.builder()
                .withPromptExecutionSettings(
                        PromptExecutionSettings.builder()
                                .withTemperature(1.0)
                                .withTopP(0.5)
                                .build())
                .withReturnMode(InvocationReturnMode.LAST_MESSAGE_ONLY)
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .build();
    }
}
