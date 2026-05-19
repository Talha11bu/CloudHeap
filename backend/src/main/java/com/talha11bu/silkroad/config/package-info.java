/**
 * Spring configuration classes for the SilkRoad application.
 *
 * <p>This package contains all {@code @Configuration} beans that wire up
 * cross-cutting infrastructure concerns:</p>
 *
 * <ul>
 *   <li>{@link com.talha11bu.silkroad.config.CorsConfig} &mdash; Global CORS policy allowing the frontend dev server.</li>
 *   <li>{@link com.talha11bu.silkroad.config.WebsocketConfig} &mdash; STOMP message broker and WebSocket endpoint registration.</li>
 *   <li>{@link com.talha11bu.silkroad.config.JwtHandshakeInterceptor} &mdash; JWT authentication for WebSocket upgrade handshakes.</li>
 *   <li>{@link com.talha11bu.silkroad.config.R2Config} &mdash; Cloudflare R2 (S3-compatible) client and pre-signer bean definitions.</li>
 *   <li>{@link com.talha11bu.silkroad.config.OpenApiConfig} &mdash; Swagger / OpenAPI specification metadata.</li>
 * </ul>
 */
package com.talha11bu.silkroad.config;
