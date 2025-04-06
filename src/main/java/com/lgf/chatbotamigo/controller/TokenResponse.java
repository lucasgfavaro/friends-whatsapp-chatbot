package com.lgf.chatbotamigo.controller;

import lombok.Getter;

import java.util.List;

@Getter
public class TokenResponse {

    List<Integer> tokens;
    Integer quantity;

    public TokenResponse(List<Integer> tokens) {
        this.tokens = tokens;
        this.quantity = tokens.size();
    }

}
