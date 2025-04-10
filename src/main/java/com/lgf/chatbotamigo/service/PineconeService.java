package com.lgf.chatbotamigo.service;

import com.lgf.chatbotamigo.persistence.dto.ChatChunk;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
public class PineconeService {
    private final VectorStore vectorStore;

    public PineconeService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void store(ChatChunk chatChunk, Boolean plan) {
        Document document = new Document(chatChunk.getMessagesAsString(),
                Map.of("chatChunkId", chatChunk.getId()
                        , "date", chatChunk.getDate()));

        log.info("Document {} to be Embedded {} ", document.getId(), document.getFormattedContent());

        if (!plan)
            vectorStore.add(List.of(document));
        else
            log.info("Plan is true, not storing the document embedding");
    }

    public List<String> searchSimilarMessages(String query) {
        return Objects.requireNonNull(vectorStore.similaritySearch(query)).stream()
                .map(Document::getFormattedContent) // Get the message content
                .toList();
    }

}
