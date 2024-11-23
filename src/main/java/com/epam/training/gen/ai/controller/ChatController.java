package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.domain.InputRequest;
import com.epam.training.gen.ai.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller()
public class ChatController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ChatService chatService;

    public ChatController(@Autowired ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> controller(@RequestBody InputRequest request) {
        logger.info("Received chat input: {}", request.getInput());
        try {
            return ResponseEntity.ok(chatService.ask(request.getInput()));
        } catch (Exception e) {
            logger.error("Error processing chat request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request.");
        }
    }
}
