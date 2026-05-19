package com.talha11bu.silkroad.config;

import com.talha11bu.silkroad.services.JwtTokenService;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket handshake interceptor that enforces JWT authentication.
 *
 * <p>Before a STOMP WebSocket connection is established, this interceptor
 * extracts the JWT token from the query parameter ({@code ?token=xxx}),
 * validates it, and injects the user's identity into the WebSocket session
 * attributes. Unauthorized connections are rejected with HTTP 401.</p>
 *
 * @see com.talha11bu.silkroad.services.JwtTokenService
 * @see com.talha11bu.silkroad.config.WebsocketConfig
 */
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtTokenService jwtTokenService;

    /**
     * Intercepts the WebSocket HTTP upgrade handshake before it completes.
     * Extracts the JWT token from the query parameters, validates it, and if successful,
     * injects the user's display name and session ID into the WebSocket attributes map.
     * This ensures only authenticated users can establish a WebSocket connection.
     *
     * @param request    The incoming HTTP upgrade request.
     * @param response   The outgoing HTTP response.
     * @param wsHandler  The target WebSocket handler.
     * @param attributes A map of attributes that will be attached to the active WebSocket session.
     * @return true if the handshake is allowed (token valid), false otherwise (unauthorized).
     * @throws Exception If an unhandled error occurs.
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // 1. Extract token from query param: ws://localhost:8080/ws?token=xxxx
        String query = request.getURI().getQuery();
        String token = null;

        if (query != null && query.contains("token=")) {
            token = query.split("token=")[1].split("&")[0];
        }

        try {
            // 2. Validate and Parse
            Claims claims = jwtTokenService.validateAndParseToken(token);

            // 3. Store identity in session attributes for the DisconnectListener
            attributes.put("displayName", claims.getSubject());
            attributes.put("sessionId", claims.get("sessionId", String.class));

            return true;
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
    }
}
