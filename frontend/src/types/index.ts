/**
 * Shared TypeScript interfaces aligned with the Spring Boot backend models.
 *
 * Backend source of truth:
 *   - CreateResponse.java:  { success, token, session, timeLeft }
 *   - JoinResponse.java:    { success, token, session, timeLeft }
 *   - Session.java:         { sessionId, password, createdAt, expiresAt, users[], files[] }
 *   - Users.java:           { id, username, token, webSocketId }
 *   - Files.java:           { id, fileName, r2Key }
 */

/** A user entity as serialized by the backend. */
export interface SessionUser {
	id: number;
	username: string;
	token: string;
	webSocketId: string;
}

/** A file entity as serialized by the backend. */
export interface SessionFile {
	id: number;
	fileName: string;
	r2Key: string;
}

/** The Session entity as serialized by the backend. */
export interface SessionState {
	sessionId: string;
	password: string;
	createdAt: string;
	expiresAt: string;
	users: SessionUser[];
	files: SessionFile[];
}

/**
 * Unified response shape for create, join, and rejoin endpoints.
 * Both CreateResponse and JoinResponse share this shape.
 */
export interface SessionResponse {
	success: boolean;
	token: string;
	session: SessionState;
	timeLeft: string;
}

/**
 * Extended session data passed via React Router location state.
 * Includes the raw API response plus a client-side injected username.
 */
export interface SessionData extends SessionResponse {
	clientUsername: string;
}

/** WebSocket notification payload from /topic/session/{sessionId}. */
export interface SessionNotification {
	type: 'USER_JOINED' | 'USER_LEFT' | 'FILE_UPLOADED' | 'FILE_DELETED';
	sessionId: string;
	payload: string;
}
