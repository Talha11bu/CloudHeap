/**
 * Service layer containing all business logic for the SilkRoad application.
 *
 * <p>Services are injected into controllers and coordinate between repositories,
 * cloud storage (R2), JWT authentication, and WebSocket notifications.</p>
 *
 * <ul>
 *   <li>{@link com.talha11bu.silkroad.services.SessionService} &mdash; Core orchestrator for session lifecycle,
 *       file management, and scheduled cleanup.</li>
 *   <li>{@link com.talha11bu.silkroad.services.R2Service} &mdash; Cloudflare R2 storage operations including
 *       uploads, downloads, pre-signed URLs, ZIP streaming, and deletions.</li>
 *   <li>{@link com.talha11bu.silkroad.services.JwtTokenService} &mdash; JWT generation and validation using
 *       HMAC-SHA256 signatures.</li>
 *   <li>{@link com.talha11bu.silkroad.services.NotiffService} &mdash; Real-time WebSocket notification
 *       broadcasting via STOMP.</li>
 *   <li>{@link com.talha11bu.silkroad.services.SessionIdGenerator} &mdash; Random, human-readable session ID
 *       generation.</li>
 * </ul>
 */
package com.talha11bu.silkroad.services;
