package com.lgf.chatbotamigo.controller;

import com.lgf.chatbotamigo.model.TokenResponse;
import com.lgf.chatbotamigo.service.ManagerService;
import jakarta.websocket.server.PathParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Log4j2
@RestController
@RequestMapping("/manager")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping("/whatsapp-file-embedding")
    public void chunkChat(@RequestParam String whatsappExportedFile, @RequestParam String plan) throws IOException {
        managerService.process(whatsappExportedFile, Boolean.parseBoolean(plan));
    }

    @PostMapping("/text-tokenize")
    public ResponseEntity<TokenResponse> tokenizeText(@RequestBody String text) {
        return ResponseEntity.ok(managerService.tokenize(text));
    }

    @GetMapping("/file-tokenize")
    public ResponseEntity<Long> tokenizeFile(@PathParam("filename") String filename) {
        return ResponseEntity.ok(managerService.tokenizeFile(filename));
    }
}
