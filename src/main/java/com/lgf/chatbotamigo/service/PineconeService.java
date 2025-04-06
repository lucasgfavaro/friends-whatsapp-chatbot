package com.lgf.chatbotamigo.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PineconeService {
    private final VectorStore vectorStore;
    private EmbeddingModel embeddingModel;

    public PineconeService(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    /**
     * Stores a message embedding in Pinecone.
     */
    public void storeMessages(Map<String, List<String>> messagesByUser) {
        for (Map.Entry<String, List<String>> entry : messagesByUser.entrySet()) {
            String user = entry.getKey();
            for (String message : entry.getValue()) {
                Document document = new Document(message, Map.of("user", user));
                vectorStore.add(List.of(document));
            }
        }
    }

    /**
     * Retrieves similar messages based on a given query.
     */
    public List<String> searchSimilarMessages(String query) {
        return vectorStore.similaritySearch(query).stream()
                .map(result -> result.getFormattedContent()) // Get the message content
                .toList();
    }

}
