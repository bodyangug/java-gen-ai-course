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

@Controller
public class ChatController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

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
