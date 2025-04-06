package com.lgf.chatbotamigo.controller;

import com.lgf.chatbotamigo.service.ChatEmbeddingService;
import com.lgf.chatbotamigo.service.ChatService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Log4j2
@RestController
@RequestMapping("/manager")
public class ManagerController {

    private final ChatEmbeddingService chatEmbeddingService;
    private final ChatService chatService;

    public ManagerController(ChatEmbeddingService chatEmbeddingService, ChatService chatService) {
        this.chatEmbeddingService = chatEmbeddingService;
        this.chatService = chatService;
    }

    @PostMapping("/upload-chat")
    public ResponseEntity<String> uploadChat(@RequestParam String file) {
        try {
            chatEmbeddingService.processAndStoreChat(file);
            return ResponseEntity.ok("Chat procesado y almacenado en Pinecone");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error procesando el chat: " + e.getMessage());
        }
    }

    @PostMapping("/chunk-chat")
    public void chunkChat(@RequestParam String file) throws IOException {
        chatService.processChat(file);
    }
}
