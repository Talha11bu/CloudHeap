package com.talha11bu.silkroad.services;

import org.springframework.stereotype.Service;

/**
 * Generates short, human-readable session identifiers.
 *
 * <p>Produces a 6-character alphanumeric string from a mixed-case seed alphabet
 * that includes letters, digits, and a few URL-safe special characters.
 * These IDs are designed to be easily shareable between users.</p>
 */
@Service
public class SessionIdGenerator {

    /**
     * Generates a random 6-character session ID.
     *
     * @return a new random session identifier (e.g., {@code "aX9.kM"}).
     */
    public String generatedId() {
        String seed = "QWERTYUIOPASDFGHJKLZXCVBNM_-.~abcdefghijklmnopqrstuvwxyz0987654321";
        StringBuilder token = new StringBuilder();
        for(int i = 0; i < 6 ; i++){
            token.append(seed.charAt((int) Math.floor(Math.random()*seed.length())));
        }
        return token.toString();
    }
}
