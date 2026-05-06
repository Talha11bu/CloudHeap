import { useState	 } from 'react';
import { useNavigate } from 'react-router-dom';
import {
	Terminal,
	Lock,
	Key,
	User,
	Loader2,
	AlertTriangle,
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import TimerDial from './HomePage/TimerDial';

const API_BASE = 'http://localhost:8080'; // Update to your backend URL

export const AuthModal = ({ onClose }: { onClose: () => void }) => {
	const [activeTab, setActiveTab] = useState<'create' | 'join'>('create');
	const [duration, setDuration] = useState(10);
	const [isLoading, setIsLoading] = useState(false);
	const [loadingText, setLoadingText] = useState('ESTABLISHING TUNNEL...');
	const [error, setError] = useState<string | null>(null);

	const navigate = useNavigate();

	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		setError(null);
		setIsLoading(true);
		setLoadingText('NEGOTIATING UPLINK...');

		const formData = new FormData(e.currentTarget);
		const inputData = Object.fromEntries(formData);

		try {
			let dataFromServer;

			if (activeTab === 'create') {
				// --- 1. CREATE LOGIC ---
				const createRes = await fetch(`${API_BASE}/sessions/create`, {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({
						username: inputData.username,
						password: inputData.password,
						duration: `PT${inputData.duration}M` 
					})
				});

				if (!createRes.ok) throw new Error("Host unreachable or server error.");
				const createData = await createRes.json();
				if (createData.sucess === false || createData.success === false) throw new Error("Failed to create session.");
				
				// Calculate local expiration timestamp to survive page refreshes
				const durationMs = parseInt(inputData.duration as string, 10) * 60 * 1000;
				const localExpiresAt = new Date(Date.now() + durationMs).toISOString();

				dataFromServer = {
					sucess: true,
					Token: createData.token, // JWT from creation
					session: {
						sessionId: createData.sessionId,
						// Use backend typos safely or fallback to what user typed
						password: createData.passowrd || inputData.password,
						expiresAt: localExpiresAt,
						users: [createData.userName || inputData.username],
						files: []
					},
					timeLeft: createData.duration || `PT${inputData.duration}M`,
					clientUsername: createData.userName || inputData.username
				};

			} else {
				// --- 2. JOIN / REJOIN LOGIC ---
				const token = localStorage.getItem('silk_road_jwt');
				let joinedSuccessfully = false;

				// Attempt Rejoin first if token exists
				if (token) {
					try {
						const rejoinRes = await fetch(`${API_BASE}/sessions/rejoin`, {
							method: 'POST',
							headers: { 'Authorization': `Bearer ${token}` }
						});

						if (rejoinRes.ok) {
							const rejoinData = await rejoinRes.json();
							if (rejoinData.sucess !== false && rejoinData.success !== false) {
								dataFromServer = rejoinData;
								joinedSuccessfully = true;
							}
						}
					} catch (err) {
						console.warn("Rejoin token expired or invalid. Falling back to manual join.");
					}
				}

				// If no token, or if Rejoin failed, do a standard Join
				if (!joinedSuccessfully) {
					const cleanSessionId = (inputData.sessionId as string).trim();

					const joinRes = await fetch(`${API_BASE}/sessions/join`, {
						method: 'POST',
						headers: { 'Content-Type': 'application/json' },
						body: JSON.stringify({
							sessionId: cleanSessionId,
							username: inputData.username,
							password: inputData.password, 
						})
					});

					if (!joinRes.ok) {
						throw new Error("Invalid Session ID, Incorrect Password, or Host Unreachable.");
					}
					
					dataFromServer = await joinRes.json();
				}
				
				// Ensure clientUsername is injected for the Join path too
				dataFromServer.clientUsername = inputData.username;
			}

			// --- 3. VALIDATE AND ROUTE ---
			if (dataFromServer.sucess === false || dataFromServer.success === false) {
				throw new Error("Server rejected the handshake.");
			}

			const jwtToken = dataFromServer.Token || dataFromServer.token || dataFromServer.JwtToke || dataFromServer.JwtToken;
			if (!jwtToken) throw new Error("Authentication token missing from server response.");
			
			localStorage.setItem('silk_road_jwt', jwtToken);

			onClose();
			navigate('/session', { state: { sessionData: dataFromServer } });

		} catch (err: any) {
			console.error("Auth Error:", err);
			setError(err.message || "CONNECTION REFUSED BY HOST");
			localStorage.removeItem('silk_road_jwt');
		} finally {
			setIsLoading(false);
		}
	};

	const fadeSlide = {
		initial: { opacity: 0, y: 10 },
		animate: { opacity: 1, y: 0 },
		exit: { opacity: 0, y: -10 },
		transition: { duration: 0.2 },
	};

	return (
		<div className='fixed inset-0 z-200 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4'>
			<div className='relative bg-neutral-950 border border-white/10 w-full max-w-4xl rounded-[2.5rem] overflow-hidden shadow-2xl flex flex-col md:flex-row min-h-125'>
				{/* LOADING OVERLAY */}
				<AnimatePresence>
					{isLoading && (
						<motion.div
							initial={{ opacity: 0 }}
							animate={{ opacity: 1 }}
							exit={{ opacity: 0 }}
							className='absolute inset-0 z-300 bg-black/90 backdrop-blur-md flex flex-col items-center justify-center'
						>
							<Loader2
								className='text-emerald-500 animate-spin mb-6'
								size={48}
							/>
							<h3 className='text-emerald-500 font-mono text-xl tracking-[0.2em] uppercase animate-pulse'>
								{loadingText}
							</h3>
						</motion.div>
					)}
				</AnimatePresence>

				{/* LEFT COLUMN: Controls */}
				<div className='w-full md:w-[45%] bg-black border-b md:border-b-0 md:border-r border-white/5 p-8 flex flex-col items-center justify-center relative overflow-hidden'>
					<div className='absolute top-0 left-0 w-full h-full bg-[radial-gradient(circle_at_center,rgba(16,185,129,0.05)_0%,transparent_70%)]' />
					<AnimatePresence mode='wait'>
						{activeTab === 'create' ? (
							<motion.div
								key='create-info'
								{...fadeSlide}
								className='relative z-10 w-full'
							>
								<div className='text-center mb-6'>
									<h3 className='text-white font-black tracking-tighter text-lg'>
										LIFESPAN
									</h3>
									<p className='text-neutral-500 font-mono text-[10px] uppercase tracking-widest'>
										Select Time-to-Live
									</p>
								</div>
								<TimerDial value={duration} onChange={(v) => setDuration(v)} />
							</motion.div>
						) : (
							<motion.div
								key='join-info'
								{...fadeSlide}
								className='relative z-10 text-center space-y-6'
							>
								<div className='w-24 h-24 mx-auto border-2 border-emerald-500/20 rounded-full flex items-center justify-center animate-[spin_10s_linear_infinite]'>
									<Terminal size={32} className='text-emerald-500/50' />
								</div>
								<h3 className='text-white font-black tracking-tighter text-lg'>
									SECURE JOIN
								</h3>
							</motion.div>
						)}
					</AnimatePresence>
				</div>

				{/* RIGHT COLUMN: Form */}
				<div className='w-full md:w-[55%] p-8 flex flex-col'>
					{/* Tab Navigation */}
					<div className='flex gap-2 mb-8 bg-white/5 p-1 rounded-xl relative'>
						<motion.div
							layout
							className='absolute top-1 bottom-1 w-[calc(50%-4px)] bg-emerald-500 rounded-lg shadow-[0_0_15px_rgba(16,185,129,0.2)]'
							animate={{ left: activeTab === 'create' ? '4px' : 'calc(50%)' }}
						/>
						<button
							type='button'
							onClick={() => {
								setActiveTab('create');
								setError(null);
							}}
							className={`relative z-10 flex-1 py-2 font-mono text-xs uppercase tracking-widest transition-colors ${activeTab === 'create' ? 'text-black font-bold' : 'text-neutral-400 hover:text-white'}`}
						>
							Create
						</button>
						<button
							type='button'
							onClick={() => {
								setActiveTab('join');
								setError(null);
							}}
							className={`relative z-10 flex-1 py-2 font-mono text-xs uppercase tracking-widest transition-colors ${activeTab === 'join' ? 'text-black font-bold' : 'text-neutral-400 hover:text-white'}`}
						>
							Connect
						</button>
					</div>

					<form
						onSubmit={handleSubmit}
						className='flex-1 flex flex-col justify-between'
					>
						{activeTab === 'create' && (
							<input type='hidden' name='duration' value={duration} />
						)}

						<div className='space-y-4'>
							<AnimatePresence mode='wait'>
								{activeTab === 'join' && (
									<motion.div
										key='session-input'
										{...fadeSlide}
										className='relative overflow-hidden'
									>
										<div className='absolute inset-y-0 left-4 flex items-center pointer-events-none'>
											<Lock size={16} className='text-neutral-500' />
										</div>
										<input
											type='text'
											name='sessionId'
											placeholder='SESSION_ID'
											required
											className='w-full bg-white/5 border border-white/10 rounded-xl pl-12 pr-4 py-3 text-white font-mono text-sm focus:border-emerald-500/50 outline-none transition-all placeholder:text-neutral-600'
										/>
									</motion.div>
								)}
							</AnimatePresence>

							<div className='relative'>
								<div className='absolute inset-y-0 left-4 flex items-center pointer-events-none'>
									<User size={16} className='text-neutral-500' />
								</div>
								<input
									type='text'
									name='username'
									placeholder='USERNAME'
									required
									className='w-full bg-white/5 border border-white/10 rounded-xl pl-12 pr-4 py-3 text-white font-mono text-sm focus:border-emerald-500/50 outline-none transition-all placeholder:text-neutral-600'
								/>
							</div>

							<div className='relative'>
								<div className='absolute inset-y-0 left-4 flex items-center pointer-events-none'>
									<Key size={16} className='text-neutral-500' />
								</div>
								<input
									type='password'
									name='password'
									placeholder='PASSWORD'
									required
									className='w-full bg-white/5 border border-white/10 rounded-xl pl-12 pr-4 py-3 text-white font-mono text-sm focus:border-emerald-500/50 outline-none transition-all placeholder:text-neutral-600'
								/>
							</div>

							{/* ERROR MESSAGE DISPLAY */}
							<AnimatePresence>
								{error && (
									<motion.div
										initial={{ opacity: 0, height: 0 }}
										animate={{ opacity: 1, height: 'auto' }}
										exit={{ opacity: 0, height: 0 }}
										className='flex items-center gap-2 text-red-500 bg-red-500/10 p-3 rounded-xl border border-red-500/20'
									>
										<AlertTriangle size={16} />
										<p className='font-mono text-xs tracking-wider uppercase'>
											{error}
										</p>
									</motion.div>
								)}
							</AnimatePresence>
						</div>

						<div className='flex gap-4 mt-8'>
							<button
								type='button'
								onClick={onClose}
								className='flex-1 px-6 py-4 rounded-xl border border-white/10 text-white font-mono text-sm hover:bg-white/5 hover:border-white/20 transition-all'
							>
								CANCEL
							</button>
							<button
								type='submit'
								className='flex-1 px-6 py-4 rounded-xl bg-emerald-500 text-black font-black uppercase tracking-widest text-sm hover:bg-emerald-400 transition-all shadow-[0_0_20px_rgba(16,185,129,0.2)]'
							>
								{activeTab === 'create' ? 'EXECUTE' : 'ENTER'}
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	);
};
