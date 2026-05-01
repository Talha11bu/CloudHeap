import api from '../../api/axiosInstance';
import type { AuthResponse } from '../../types';

export const authApi = {
	joinSession: async (sessionId: string, name: string, password?: string) => {
		const response = await api.post<AuthResponse>(
			`/sessions/${sessionId}/join`,
			{
				name,
				password,
			},
		);
		return response.data;
	},

	createSession: async (name: string, password?: string) => {
		const response = await api.post<AuthResponse>('/sessions', {
			name,
			password,
		});
		return response.data;
	},

	rejoinSession: async () => {
		const response = await api.post<AuthResponse>('/sessions/rejoin');
		return response.data;
	},
};
