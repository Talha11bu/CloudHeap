package com.talha11bu.cloudheap.services;

import org.springframework.stereotype.Service;

@Service
public class SessionIdGenerator {

    public String generatedId() {
        String seed = "QWERTYUIOPASDFGHJKLZXCVBNMabcdefghijklmnopqrstuvwxyz0987654321";
        StringBuilder token = new StringBuilder();
        int limit = 6;
        for(int i = 0; i <= limit ; i++){
            token.append(seed.charAt((int) Math.floor(Math.random()*seed.length())));
        }
        return token.toString();
    }
}
