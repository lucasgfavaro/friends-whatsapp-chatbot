package com.lgf.chatbotamigo.controller;

import com.lgf.chatbotamigo.model.Message;
import com.lgf.chatbotamigo.persistence.dto.ChatChunk;
import com.lgf.chatbotamigo.service.ManagerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chats")
public class ChatChunkController {

    private final ManagerService chatChunkService;

    public ChatChunkController(ManagerService chatChunkService) {
        this.chatChunkService = chatChunkService;
    }

    @PostMapping
    public String create(@RequestBody Message message) {
        return chatChunkService.createChatChunk(message.getSender(), message.getText());
    }

    @GetMapping("/{id}")
    public ChatChunk getBy(@PathVariable String id) {
        return chatChunkService.findById(id).orElse(null);
    }
}