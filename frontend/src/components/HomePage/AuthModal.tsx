import { useState, useEffect } from 'react';
import { Terminal, Lock, Key, User, Loader2 } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import TimerDial from './TimerDial'; // Adjust import path as needed

export const AuthModal = ({ onClose }: { onClose: () => void }) => {
	const [activeTab, setActiveTab] = useState<'create' | 'join'>('create');
	const [duration, setDuration] = useState(10);
	const [isLoading, setIsLoading] = useState(false);
	const [loadingText, setLoadingText] = useState('ESTABLISHING TUNNEL...');

	useEffect(() => {
		const token = localStorage.getItem('silk_road_jwt');
		if (token) {
			setLoadingText('RE-ESTABLISHING SECURE LINK...');
			setIsLoading(true);
			setTimeout(() => {
				console.log('Rejoined using stored JWT:', token);
				setIsLoading(false);
			}, 2000);
		}
	}, []);

	const handleAction = async (formData: FormData) => {
		setIsLoading(true);

		if (activeTab === 'create') {
			setLoadingText('GENERATING SESSION PROTOCOLS...');
			const payload = {
				username: formData.get('username'),
				password: formData.get('password'),
				duration: Number(formData.get('duration')),
			};
			console.log('Creating Session:', payload);
			await new Promise((resolve) => setTimeout(resolve, 2000));
		} else {
			setLoadingText('AUTHENTICATING UPLINK...');
			const payload = {
				sessionId: formData.get('sessionId'),
				username: formData.get('username'),
				password: formData.get('password'),
			};
			console.log('Joining Session:', payload);
			await new Promise((resolve) => setTimeout(resolve, 2000));
		}

		setIsLoading(false);
	};

	// Reusable animation variants
	const fadeSlide = {
		initial: { opacity: 0, y: 10 },
		animate: { opacity: 1, y: 0 },
		exit: { opacity: 0, y: -10 },
		transition: { duration: 0.2 },
	};

	return (
		<div className='fixed inset-0 z-200 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4'>
			<div className='relative bg-neutral-950 border border-white/10 w-full max-w-4xl rounded-[2.5rem] overflow-hidden shadow-2xl flex flex-col md:flex-row min-h-[500px]'>
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
							<p className='text-neutral-500 font-mono text-xs mt-2'>
								Standby for handshake confirmation
							</p>
						</motion.div>
					)}
				</AnimatePresence>

				{/* LEFT COLUMN: Controls & Info */}
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
								<div>
									<h3 className='text-white font-black tracking-tighter text-lg'>
										SECURE JOIN
									</h3>
									<p className='text-neutral-500 font-mono text-[10px] uppercase tracking-widest leading-relaxed mt-2'>
										Requires active Session ID <br /> and authorized
										credentials.
									</p>
								</div>
							</motion.div>
						)}
					</AnimatePresence>
				</div>

				{/* RIGHT COLUMN: Forms */}
				<div className='w-full md:w-[55%] p-8 flex flex-col'>
					{/* Tab Navigation */}
					<div className='flex gap-2 mb-8 bg-white/5 p-1 rounded-xl relative'>
						{/* Animated Background Pill */}
						<motion.div
							layout
							className='absolute top-1 bottom-1 w-[calc(50%-4px)] bg-emerald-500 rounded-lg shadow-[0_0_15px_rgba(16,185,129,0.2)]'
							animate={{ left: activeTab === 'create' ? '4px' : 'calc(50%)' }}
							transition={{ type: 'spring', stiffness: 300, damping: 30 }}
						/>

						<button
							type='button'
							onClick={() => setActiveTab('create')}
							className={`relative z-10 flex-1 py-2 rounded-lg font-mono text-xs uppercase tracking-widest transition-colors ${
								activeTab === 'create'
									? 'text-black font-bold'
									: 'text-neutral-400 hover:text-white'
							}`}
						>
							Initialize
						</button>
						<button
							type='button'
							onClick={() => setActiveTab('join')}
							className={`relative z-10 flex-1 py-2 rounded-lg font-mono text-xs uppercase tracking-widest transition-colors ${
								activeTab === 'join'
									? 'text-black font-bold'
									: 'text-neutral-400 hover:text-white'
							}`}
						>
							Connect
						</button>
					</div>

					{/* Form Container */}
					<form
						action={handleAction}
						className='flex-1 flex flex-col justify-between'
					>
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

							{/* Shared Fields */}
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

							{activeTab === 'create' && (
								<input type='hidden' name='duration' value={duration} />
							)}
						</div>

						{/* Action Buttons */}
						<div className='flex gap-4 mt-8'>
							<button
								type='button'
								onClick={onClose}
								className='flex-1 px-6 py-4 rounded-xl border border-white/10 text-white font-mono text-sm hover:bg-white/5 hover:border-white/20 transition-all'
							>
								ABORT
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
