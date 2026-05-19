import { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import { WS_BASE } from '../config';
import type { SessionNotification } from '../types';

/**
 * Custom hook encapsulating the full STOMP WebSocket lifecycle.
 * Connects on mount, subscribes to the session topic, dispatches state
 * updates for user/file events, and disconnects on unmount.
 */
export function useSessionWebSocket(
	sessionId: string | undefined,
	initialUsers: string[],
	initialFiles: string[],
	onSessionEnded: () => void
) {
	const [users, setUsers] = useState<string[]>(initialUsers);
	const [files, setFiles] = useState<string[]>(initialFiles);
	const stompClient = useRef<Client | null>(null);

	const onSessionEndedRef = useRef(onSessionEnded);
	useEffect(() => {
		onSessionEndedRef.current = onSessionEnded;
	}, [onSessionEnded]);

	useEffect(() => {
		if (!sessionId) return;

		const token = localStorage.getItem('silk_road_jwt');

		const client = new Client({
			brokerURL: `${WS_BASE}?token=${token}`,
			reconnectDelay: 5000,
			onConnect: () => {
				console.log('WebSocket Connected!');

				client.subscribe(`/topic/session/${sessionId}`, (message) => {
					// Raw text termination message from NotiffService
					if (message.body.includes('successfully ended')) {
						onSessionEndedRef.current();
						return;
					}

					try {
						const data: SessionNotification = JSON.parse(message.body);

						switch (data.type) {
							case 'USER_JOINED':
								setUsers((prev) => [...new Set([...prev, data.payload])]);
								break;
							case 'USER_LEFT':
								setUsers((prev) => prev.filter((u) => u !== data.payload));
								break;
							case 'FILE_UPLOADED':
								setFiles((prev) => [...new Set([...prev, data.payload])]);
								break;
							case 'FILE_DELETED':
								setFiles((prev) => prev.filter((f) => f !== data.payload));
								break;
						}
					} catch {
						console.error('Failed to parse WebSocket message:', message.body);
					}
				});
			},
			onWebSocketError: (error) => {
				console.error('WebSocket Error:', error);
			},
		});

		client.activate();
		stompClient.current = client;

		return () => {
			if (stompClient.current) {
				stompClient.current.deactivate();
			}
		};
	}, [sessionId]);

	const disconnect = () => {
		if (stompClient.current) stompClient.current.deactivate();
	};

	return { users, files, disconnect };
}
