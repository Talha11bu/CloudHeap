package com.talha11bu.silkroad.services;

import org.springframework.stereotype.Service;

@Service
public class SessionIdGenerator {

    public String generatedId() {
        String seed = "QWERTYUIOPASDFGHJKLZXCVBNM!@#$%^&*?:~-+=abcdefghijklmnopqrstuvwxyz0987654321";
        StringBuilder token = new StringBuilder();
        for(int i = 0; i < 6 ; i++){
            token.append(seed.charAt((int) Math.floor(Math.random()*seed.length())));
        }
        return token.toString();
    }
}
