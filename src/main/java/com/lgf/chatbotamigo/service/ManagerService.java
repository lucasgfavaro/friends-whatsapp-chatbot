package com.lgf.chatbotamigo.service;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import com.lgf.chatbotamigo.model.Message;
import com.lgf.chatbotamigo.model.TokenResponse;
import com.lgf.chatbotamigo.persistence.dto.ChatChunk;
import com.lgf.chatbotamigo.persistence.repository.ChatChunkRepository;
import com.lgf.chatbotamigo.util.WhatsappChunker;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class ManagerService {

    private final ChatChunkRepository chatChunkRepository;
    private final PineconeService pineconeService;
    private final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    private final Encoding encoding = registry.getEncodingForModel(ModelType.TEXT_EMBEDDING_ADA_002);

    public ManagerService(ChatChunkRepository chatChunkRepository, PineconeService pineconeService) {
        this.chatChunkRepository = chatChunkRepository;
        this.pineconeService = pineconeService;
        // Initialize the tokenizer registry
        registry.getEncodingForModel(ModelType.TEXT_EMBEDDING_ADA_002);
    }

    public void process(String whatsappExportedFile, Boolean plan) throws IOException {

        WhatsappChunker.chunkChat(whatsappExportedFile).forEach(chunk -> {
            ChatChunk chatChunk = new ChatChunk(UUID.randomUUID().toString(), chunk.getStartTime().toString(), chunk.getMessages());
            if (!plan)
                chatChunkRepository.save(chatChunk);
            pineconeService.store(chatChunk, plan);
        });
    }

    public String createChatChunk(String from, String text) {
        ChatChunk chat = new ChatChunk(UUID.randomUUID().toString(), LocalDateTime.now().toString(), null);
        chat.getMessages().add(new Message(LocalDateTime.now(), from, text));
        return chatChunkRepository.save(chat).getId();
    }

    public Optional<ChatChunk> findById(String id) {
        return chatChunkRepository.findById(id);
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
