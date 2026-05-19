package com.talha11bu.silkroad.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket and STOMP message broker configuration.
 *
 * <p>Enables a simple in-memory message broker with subscriptions under
 * {@code /topic} and registers the STOMP endpoint at {@code /ws}.
 * Clients connect to {@code ws://host/ws} and subscribe to
 * {@code /topic/session/{sessionId}} for real-time event notifications.</p>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker that routes messages from one client to another.
     * Enables a simple in-memory broker for subscriptions starting with "/topic".
     *
     * @param config The MessageBrokerRegistry to configure.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");// subscribe to: /topic/sessions/{sessionId}
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the initial HTTP endpoint that clients will use to upgrade to a WebSocket connection.
     * Maps to "/ws" and allows CORS from the local frontend dev server.
     *
     * @param registry The StompEndpointRegistry to register the endpoint.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173");
    }

}
