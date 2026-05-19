/**
 * REST controllers exposing the SilkRoad HTTP API.
 *
 * <p>All endpoints are mapped under {@code /sessions} and documented with
 * Swagger annotations for interactive exploration via Swagger UI.</p>
 *
 * <ul>
 *   <li>{@link com.talha11bu.silkroad.controller.SessionController} &mdash; Session lifecycle and file operations
 *       (create, join, rejoin, leave, end, upload, download, delete).</li>
 *   <li>{@link com.talha11bu.silkroad.controller.HealthController} &mdash; Unauthenticated health check endpoint.</li>
 * </ul>
 *
 * <p>Controllers delegate all business logic to
 * {@link com.talha11bu.silkroad.services.SessionService} and return
 * appropriate HTTP status codes based on the service response.</p>
 */
package com.talha11bu.silkroad.controller;
