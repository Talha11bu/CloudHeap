/**
 * API functions for session lifecycle operations (create, join, rejoin).
 */
import { API_BASE } from '../config';
import type { SessionResponse } from '../types';

/** Creates a new session. Returns the full session response with JWT. */
export async function createSession(
	username: string,
	password: string,
	durationMinutes: number
): Promise<SessionResponse> {
	const res = await fetch(`${API_BASE}/sessions/create`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			username,
			password,
			duration: `PT${durationMinutes}M`,
		}),
	});

	if (!res.ok) throw new Error('Host unreachable or server error.');

	const data: SessionResponse = await res.json();
	if (!data.success) throw new Error('Failed to create session.');

	return data;
}

/** Joins an existing session by ID and password. */
export async function joinSession(
	sessionId: string,
	username: string,
	password: string
): Promise<SessionResponse> {
	const res = await fetch(`${API_BASE}/sessions/join`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			sessionId: sessionId.trim(),
			username,
			password,
		}),
	});

	if (!res.ok)
		throw new Error(
			'Invalid Session ID, Incorrect Password, or Host Unreachable.'
		);

	const data: SessionResponse = await res.json();
	if (!data.success) throw new Error('Server rejected the handshake.');

	return data;
}

/** Attempts to rejoin a session using an existing JWT token. Returns null if the token is expired/invalid. */
export async function rejoinSession(
	token: string
): Promise<SessionResponse | null> {
	try {
		const res = await fetch(`${API_BASE}/sessions/rejoin`, {
			method: 'POST',
			headers: { Authorization: `Bearer ${token}` },
		});

		if (!res.ok) return null;

		const data: SessionResponse = await res.json();
		if (!data.success) return null;

		return data;
	} catch {
		return null;
	}
}

/** Ends/leaves a session depending on whether the user is the host. */
export async function exitSession(
	sessionId: string,
	username: string,
	isHost: boolean
): Promise<void> {
	const endpoint = isHost
		? `${API_BASE}/sessions/${sessionId}?username=${username}`
		: `${API_BASE}/sessions/${sessionId}/leave?username=${username}`;

	await fetch(endpoint, { method: 'DELETE' });
}
