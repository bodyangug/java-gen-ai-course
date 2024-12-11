package com.epam.training.gen.ai.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service class for managing chat interactions.
 * This class handles user chat histories, selects the appropriate AI model, and processes chat requests to generate responses.
 */
@Service
public class ChatService {

    private final Kernel kernel;
    private final InvocationContext invocationContext;
    private final ConcurrentHashMap<String, ChatHistory> userChatHistories = new ConcurrentHashMap<>();
    private final Map<String, ChatCompletionService> chatCompletionServices = new ConcurrentHashMap<>();

    /**
     * Constructs a new {@link ChatService} with the specified kernel, invocation context,
     * and a list of chat completion services.
     *
     * @param kernel                    the kernel instance used for executing AI models
     * @param invocationContext         the context for executing prompts and chat tasks
     * @param chatCompletionServiceList the list of available chat completion services, each associated with a model
     */
    @Autowired
    public ChatService(
            Kernel kernel,
            InvocationContext invocationContext,
            List<ChatCompletionService> chatCompletionServiceList) {
        this.kernel = kernel;
        this.invocationContext = invocationContext;

        for (ChatCompletionService service : chatCompletionServiceList) {
            this.chatCompletionServices.put(service.getModelId(), service);
        }
    }

    /**
     * Processes a chat question from a user and generates a response using the specified AI model.
     *
     * @param userId   the unique identifier of the user
     * @param question the chat question or input provided by the user
     * @param modelId  the identifier of the AI model to be used
     * @return the AI-generated response to the user's question
     * @throws IllegalArgumentException if the specified model ID is not found
     */
    public String ask(String userId, String question, String modelId) {
        // Retrieve the chat completion service for the given model ID
        ChatCompletionService chatCompletionService = chatCompletionServices.get(modelId);
        if (chatCompletionService == null) {
            throw new IllegalArgumentException("Model not found: " + modelId);
        }

        // Retrieve or create a new chat history for the user
        ChatHistory chatHistory =
                userChatHistories.computeIfAbsent(userId, id -> new ChatHistory("You are a friendly helper."));
        chatHistory.addUserMessage(question);

        String reply = chatCompletionService.getChatMessageContentsAsync(chatHistory, kernel, invocationContext)
                .block()
                .stream()
                .map(ChatMessageContent::getContent)
                .collect(Collectors.joining());

        chatHistory.addAssistantMessage(reply);
        return reply;
    }
}
