/**
 * Root package for the SilkRoad application.
 *
 * <p>SilkRoad is an ephemeral, real-time file-sharing platform that creates
 * temporary, password-protected sessions for secure data exchange. Sessions
 * auto-expire after a configurable duration, leaving no persistent traces.</p>
 *
 * <h2>Package Structure</h2>
 * <ul>
 *   <li>{@link com.talha11bu.silkroad.config} &mdash; Spring configuration beans (CORS, WebSocket, R2, OpenAPI).</li>
 *   <li>{@link com.talha11bu.silkroad.controller} &mdash; REST controllers exposing the HTTP API.</li>
 *   <li>{@link com.talha11bu.silkroad.model} &mdash; JPA entities and request/response records.</li>
 *   <li>{@link com.talha11bu.silkroad.repo} &mdash; Spring Data JPA repositories.</li>
 *   <li>{@link com.talha11bu.silkroad.services} &mdash; Business logic, JWT handling, R2 storage, and notifications.</li>
 * </ul>
 *
 * <h2>Architecture</h2>
 * <ol>
 *   <li>A user creates a session (duration + password) and receives a JWT.</li>
 *   <li>Other users join via Session ID and password, also receiving JWTs.</li>
 *   <li>All users connect to a WebSocket topic for real-time sync.</li>
 *   <li>Files are uploaded directly to Cloudflare R2 via pre-signed URLs.</li>
 *   <li>A scheduled job cleans up expired and abandoned sessions automatically.</li>
 * </ol>
 */
package com.talha11bu.silkroad;
