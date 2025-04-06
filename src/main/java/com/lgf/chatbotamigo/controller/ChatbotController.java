package com.lgf.chatbotamigo.controller;

import com.lgf.chatbotamigo.service.FriendChatAssistant;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    private final FriendChatAssistant friendChatAssistant;

    public ChatbotController(FriendChatAssistant friendChatAssistant) {
        this.friendChatAssistant = friendChatAssistant;
    }

    @GetMapping("/chat")
    public String question(@RequestParam UUID chatId, @RequestParam String fromFriend,
                           @RequestParam String toFriend, @RequestParam String question) {
        return friendChatAssistant.chat(chatId.toString(), fromFriend, toFriend, question);
    }

}
