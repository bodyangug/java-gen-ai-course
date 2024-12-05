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

@Service
public class ChatService {
    private final Kernel kernel;
    private final InvocationContext invocationContext;
    private final ConcurrentHashMap<String, ChatHistory> userChatHistories = new ConcurrentHashMap<>();
    private final Map<String, ChatCompletionService> chatCompletionServices = new ConcurrentHashMap<>();

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

    public String ask(String userId, String question, String modelId) {
        ChatCompletionService chatCompletionService = chatCompletionServices.get(modelId);

        if (chatCompletionService == null) {
            throw new IllegalArgumentException("Model not found: " + modelId);
        }

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
