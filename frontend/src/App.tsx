import { useAuthStore } from './store/useAuthStore';
import HomePage from './components/HomePage';
import Navbar from './components/HomePage/NavBar';
import './index.css';

function App() {
	const { token } = useAuthStore();

	return (
		<div className='bg-neutral-950 min-h-screen text-neutral-100 font-sans selection:bg-emerald-500/30'>
			<div className='bg-glow' />
			<Navbar />
			{!token ? (
				<HomePage />
			) : (
				<div className='p-20 text-center'>Session Page Placeholder</div>
			)}
		</div>
	);
}

export default App;
