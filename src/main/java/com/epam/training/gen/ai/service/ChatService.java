package com.epam.training.gen.ai.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatCompletionService chatCompletionService;
    private final Kernel kernel;
    private final InvocationContext invocationContext;
    private final ChatHistory chatHistory;

    public ChatService(
            @Autowired Kernel kernel,
            @Autowired InvocationContext invocationContext,
            @Autowired ChatCompletionService chatCompletionService,
            @Autowired ChatHistory chatHistory) {
        this.kernel = kernel;
        this.invocationContext = invocationContext;
        this.chatCompletionService = chatCompletionService;
        this.chatHistory = chatHistory;
    }

    public String ask(String question) {
        chatHistory.addUserMessage(question);
        List<ChatMessageContent<?>> reply =
                chatCompletionService.getChatMessageContentsAsync(chatHistory, kernel, invocationContext)
                        .block();
        return reply.stream()
                .map(ChatMessageContent::getContent)
                .collect(Collectors.joining());
    }
}
