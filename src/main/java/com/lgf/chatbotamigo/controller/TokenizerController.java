package com.lgf.chatbotamigo.controller;


import com.lgf.chatbotamigo.service.TokenizerService;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
@AllArgsConstructor
public class TokenizerController {

    private final TokenizerService tokenizerService;

    @PostMapping
    public ResponseEntity<TokenResponse> tokenizeText(@RequestBody String text) {
        return ResponseEntity.ok(tokenizerService.tokenize(text));
    }

    @GetMapping("/file")
    public ResponseEntity<Long> tokenizeFile(@PathParam("filename") String filename) {
        return ResponseEntity.ok(tokenizerService.tokenizeFile(filename));
    }
}
