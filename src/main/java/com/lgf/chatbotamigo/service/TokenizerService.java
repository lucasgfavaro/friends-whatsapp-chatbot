package com.lgf.chatbotamigo.service;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import com.lgf.chatbotamigo.controller.TokenResponse;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
public class TokenizerService {

    private final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    private final Encoding encoding = registry.getEncodingForModel(ModelType.TEXT_EMBEDDING_ADA_002);

    public TokenizerService() {
        // Initialize the tokenizer registry
        registry.getEncodingForModel(ModelType.TEXT_EMBEDDING_ADA_002);
    }

    public TokenResponse tokenize(String text) {
        return new TokenResponse(encoding.encode(text).boxed());
    }

    public Long tokenizeFile(String filename) {
        List<String> lines = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(WhatsappChunker.class.getClassLoader().getResourceAsStream(filename)),
                        StandardCharsets.UTF_8
                )).lines().toList();

        return lines.stream().map(text -> encoding.encode(text).size()).reduce(Integer::sum).orElse(0).longValue();

    }
}
