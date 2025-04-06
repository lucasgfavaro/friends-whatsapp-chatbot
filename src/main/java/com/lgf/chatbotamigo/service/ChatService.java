package com.lgf.chatbotamigo.service;

import com.lgf.chatbotamigo.model.Message;
import com.lgf.chatbotamigo.persistence.dto.Chat;
import com.lgf.chatbotamigo.persistence.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final PineconeService pineconeService;

    public String create(String from, String text) {
        Chat chat = new Chat(UUID.randomUUID().toString(), LocalDateTime.now().toString(), null);
        chat.getMessages().add(new Message(LocalDateTime.now(), from, text));
        return chatRepository.save(chat).getId();
    }

    public Optional<Chat> findById(String id) {
        return chatRepository.findById(id);
    }

    public void processChat(String filename) throws IOException {
        List<WhatsappChunker.Chunk> chunks = WhatsappChunker.chunkChat(filename);

        for (WhatsappChunker.Chunk chunk : chunks) {
            //pineconeService.storeMessages();
            //chatRepository.save(new Chat(UUID.randomUUID().toString(), chunk.startTime.toString(), chunk.messages));
        }
    }
}
