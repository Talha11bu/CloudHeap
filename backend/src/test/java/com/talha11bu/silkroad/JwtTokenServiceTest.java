package com.talha11bu.silkroad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.talha11bu.silkroad.services.JwtTokenService;

import io.jsonwebtoken.Claims;

@SpringBootTest
class JwtTokenServiceTest {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Test
    void shouldGenerateAndValidateToken() {
        String sessionId = "test-session";
        String name = "Tester";

        String token = jwtTokenService.generateToken(sessionId, name);
        assertNotNull(token);

        Claims claims = jwtTokenService.validateAndParseToken(token);
        assertEquals(name, claims.getSubject());
        assertEquals(sessionId, claims.get("sid"));
    }

    @Test
    void shouldThrowExceptionOnInvalidToken() {
        assertThrows(Exception.class, () -> jwtTokenService.validateAndParseToken("invalid.token.here"));
    }
}


