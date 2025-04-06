package com.lgf.chatbotamigo.service;

import com.lgf.chatbotamigo.util.WhatsAppChatParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class ChatEmbeddingService {

    private final PineconeService pineconeService;

    @Autowired
    public ChatEmbeddingService(PineconeService pineconeService) {
        this.pineconeService = pineconeService;
    }

    public void processAndStoreChat(String filePath) throws Exception {
        Map<String, List<String>> messagesByUser = WhatsAppChatParser.parseChatFile(filePath);

        for (Map.Entry<String, List<String>> entry : messagesByUser.entrySet()) {
            String user = entry.getKey();

            for (String message : entry.getValue()) {
                pineconeService.storeMessages(Map.of(user, List.of(message)));
            }
        }
    }
}
