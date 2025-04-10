package com.lgf.chatbotamigo.controller;

import com.lgf.chatbotamigo.service.FriendChatbotService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    private final FriendChatbotService friendChatAssistant;

    public ChatbotController(FriendChatbotService friendChatAssistant) {
        this.friendChatAssistant = friendChatAssistant;
    }

    @GetMapping("/question")
    public String question(@RequestParam String fromFriend, @RequestParam String toFriend, @RequestParam String question) {
        return friendChatAssistant.askQuestion(fromFriend, toFriend, question);
    }

    @GetMapping("/conversation")
    public String conversation(@RequestParam String topic) {
        return friendChatAssistant.generateConversation(topic);
    }

}
