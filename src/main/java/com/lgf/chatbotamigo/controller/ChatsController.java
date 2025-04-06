package com.lgf.chatbotamigo.controller;

import com.lgf.chatbotamigo.model.Message;
import com.lgf.chatbotamigo.persistence.dto.Chat;
import com.lgf.chatbotamigo.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chats")
public class ChatsController {

    private final ChatService chatService;

    public ChatsController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public String create(@RequestBody Message message) {
        return chatService.create(message.getSender(), message.getText());
    }

    @GetMapping("/{id}")
    public Chat getBy(@PathVariable String id) {
        return chatService.findById(id).orElse(null);
    }
}