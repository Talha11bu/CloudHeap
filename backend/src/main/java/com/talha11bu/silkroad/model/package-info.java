/**
 * Domain model classes for the SilkRoad application.
 *
 * <p>Contains JPA entities representing persistent state and Java records
 * serving as request/response DTOs for the REST API.</p>
 *
 * <h2>JPA Entities</h2>
 * <ul>
 *   <li>{@link com.talha11bu.silkroad.model.Session} &mdash; An ephemeral file-sharing session with password protection and auto-expiration.</li>
 *   <li>{@link com.talha11bu.silkroad.model.Users} &mdash; A user participating in a session, identified by JWT.</li>
 *   <li>{@link com.talha11bu.silkroad.model.Files} &mdash; A file uploaded to a session, stored in Cloudflare R2.</li>
 * </ul>
 *
 * <h2>Request Records</h2>
 * <ul>
 *   <li>{@link com.talha11bu.silkroad.model.CreateRequest} &mdash; Payload for creating a new session.</li>
 *   <li>{@link com.talha11bu.silkroad.model.JoinRequest} &mdash; Payload for joining an existing session.</li>
 *   <li>{@link com.talha11bu.silkroad.model.UploadRequest} &mdash; Legacy server-proxied file upload payload.</li>
 *   <li>{@link com.talha11bu.silkroad.model.UploadCompleteRequest} &mdash; Confirmation payload after direct-to-R2 upload.</li>
 * </ul>
 *
 * <h2>Response Records</h2>
 * <ul>
 *   <li>{@link com.talha11bu.silkroad.model.CreateResponse} &mdash; Returned after session creation.</li>
 *   <li>{@link com.talha11bu.silkroad.model.JoinResponse} &mdash; Returned after join or rejoin.</li>
 *   <li>{@link com.talha11bu.silkroad.model.UploadResponse} &mdash; Returned after successful file upload.</li>
 * </ul>
 *
 * <h2>WebSocket</h2>
 * <ul>
 *   <li>{@link com.talha11bu.silkroad.model.SessionNotiff} &mdash; Notification payload broadcast over STOMP.</li>
 * </ul>
 */
package com.talha11bu.silkroad.model;
