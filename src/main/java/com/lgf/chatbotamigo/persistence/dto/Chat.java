package com.lgf.chatbotamigo.persistence.dto;

import com.lgf.chatbotamigo.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "chats")
@Getter
@AllArgsConstructor
public class Chat {
    @Id
    private String id;
    private String date;
    private List<Message> messages;

    public List<Message> getMessages() {
        if (messages == null)
            return new ArrayList<>();
        return messages;
    }
}