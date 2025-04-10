package com.lgf.chatbotamigo.persistence.repository;

import com.lgf.chatbotamigo.persistence.dto.ChatChunk;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatChunkRepository extends MongoRepository<ChatChunk, String> {

}