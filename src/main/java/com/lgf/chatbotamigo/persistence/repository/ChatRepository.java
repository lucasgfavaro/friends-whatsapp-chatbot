package com.lgf.chatbotamigo.persistence.repository;

import com.lgf.chatbotamigo.persistence.dto.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {

}