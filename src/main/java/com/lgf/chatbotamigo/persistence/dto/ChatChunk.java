package com.lgf.chatbotamigo.persistence.dto;

import com.lgf.chatbotamigo.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "chatChunks")
@Getter
@AllArgsConstructor
public class ChatChunk {
    @Id
    private String id;
    private String date;
    private List<Message> messages;

    public List<Message> getMessages() {
        if (messages == null)
            return new ArrayList<>();
        return messages;
    }

    public String getMessagesAsString() {
        return getMessages().stream()
                .map(Message::toString)
                .reduce("", (a, b) -> a + "\n" + b);
    }

}