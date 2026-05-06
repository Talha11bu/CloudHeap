import { createBrowserRouter, redirect } from 'react-router-dom';
import App from './App';
import { SessionPage } from './components/SessionPage';
import { HomePage } from './components/HomePage';

export const router = createBrowserRouter([
	{
		path: '/',
		element: <App />,
		children: [
			{ index: true, element: <HomePage /> },
			{
				path: 'session',
				element: <SessionPage />,
				loader: async ({ request }) => {
					const token = localStorage.getItem('silk_road_jwt');
					if (!token) return redirect('/');
					return { token };
				},
			},
		],
		action: async ({ request }) => {
			const formData = await request.formData();
			const mode = formData.get('intent'); // 'create' or 'join'
			const payload = Object.fromEntries(formData);

			try {
				const response = await fetch(
					`http://localhost:8080/api/session/${mode}`, // CHange later with real URL
					{
						method: 'POST',
						headers: { 'Content-Type': 'application/json' },
						body: JSON.stringify(payload),
					},
				);

				if (!response.ok) {
					const errorData = await response.json();
					return { error: errorData.message || 'HANDSHAKE_DENIED' };
				}

				const { jwt, sessionId } = await response.json();
				localStorage.setItem('silk_road_jwt', jwt);

				return redirect(`/session?id=${sessionId}`);
			} catch (err) {
				return { error: 'SERVER_UNREACHABLE' };
			}
		},
	},
]);
