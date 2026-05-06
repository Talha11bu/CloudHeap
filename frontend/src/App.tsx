import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { HomePage } from './components/HomePage';
import { SessionPage } from './components/SessionPage';
import { AuthModal } from './components/AuthModal';
import { useState } from 'react';
import './index.css';

export default function App() {
	const [isModalOpen, setIsModalOpen] = useState(false);

	return (
		<BrowserRouter>
			<div className='min-h-screen bg-[#050505] text-white relative overflow-hidden'>
				<div className='fixed inset-0 pointer-events-none bg-[radial-gradient(circle_at_50%_50%,rgba(16,185,129,0.03)_0%,transparent_80%)]' />

				<Routes>
					<Route
						path='/'
						element={<HomePage onGetStarted={() => setIsModalOpen(true)} />}
					/>
					<Route path='/session' element={<SessionPage />} />
				</Routes>

				{isModalOpen && <AuthModal onClose={() => setIsModalOpen(false)} />}
			</div>
		</BrowserRouter>
	);
}
