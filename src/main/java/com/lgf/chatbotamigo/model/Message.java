package com.lgf.chatbotamigo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Message {
    private LocalDateTime timestamp;
    private String sender;
    private String text;

    @Override
    public String toString() {
        return timestamp + " - " + sender + ": " + text;
    }

}