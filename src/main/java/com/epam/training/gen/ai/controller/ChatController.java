package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.domain.InputRequest;
import com.epam.training.gen.ai.domain.InputResponse;
import com.epam.training.gen.ai.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller class for handling chat-related API requests.
 * This class provides an endpoint for sending chat input to a service and returning the generated response.
 */
@Controller
public class ChatController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ChatService chatService;

    /**
     * Constructs a new {@link ChatController} with the given {@link ChatService}.
     *
     * @param chatService the service responsible for handling chat logic
     */
    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Handles POST requests to the /chat endpoint.
     * Accepts a chat input request, processes it using the chat service, and returns the response.
     *
     * @param request the input request containing user ID, input text, and model ID
     * @return a {@link ResponseEntity} containing the chat response or an error message
     */
    @PostMapping("/chat")
    public ResponseEntity<InputResponse> controller(@RequestBody InputRequest request) {
        logger.info("Received chat input: {}", request.getInput());
        try {
            String response = chatService.ask(request.getUserId(), request.getInput(), request.getModelId());
            return ResponseEntity.ok(new InputResponse(response));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid model ID", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new InputResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error processing chat request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InputResponse("An error occurred while processing your request."));
        }
    }
}

