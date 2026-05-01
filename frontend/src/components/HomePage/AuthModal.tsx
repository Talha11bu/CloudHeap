import { motion, AnimatePresence } from 'framer-motion';
import { useState } from 'react';

const AuthModal = ({ onClose }: { onClose: () => void }) => {
	const [isJoining, setIsJoining] = useState(true);

	return (
		<AnimatePresence>
			<div className='fixed inset-0 z-60 flex items-center justify-center p-4'>
				{/* Backdrop Fade */}
				<motion.div
					initial={{ opacity: 0 }}
					animate={{ opacity: 1 }}
					exit={{ opacity: 0 }}
					onClick={onClose}
					className='absolute inset-0 bg-black/90 backdrop-blur-sm'
				/>

				{/* Modal Pop & Morph */}
				<motion.div
					layout // 🔑 This magic prop handles the height/width transition
					initial={{ opacity: 0, scale: 0.95, y: 20 }}
					animate={{ opacity: 1, scale: 1, y: 0 }}
					exit={{ opacity: 0, scale: 0.95, y: 20 }}
					transition={{ type: 'spring', damping: 25, stiffness: 300 }}
					className='relative w-full max-w-md bg-neutral-900 border border-white/5 rounded-3xl p-8 shadow-2xl overflow-hidden'
				>
					<div className='relative flex p-1 bg-black/40 rounded-xl mb-8 border border-white/5'>
						<motion.div
							layoutId='activeTab' // 🔑 Animates the green box sliding across
							className={`absolute top-1 bottom-1 w-[calc(50%-4px)] bg-emerald-500 rounded-lg ${isJoining ? 'left-1' : 'left-[calc(50%+1px)]'}`}
							transition={{ type: 'spring', bounce: 0.2, duration: 0.6 }}
						/>
						<button
							onClick={() => setIsJoining(true)}
							className={`relative z-10 flex-1 py-2 text-sm font-bold transition-colors ${isJoining ? 'text-black' : 'text-neutral-500'}`}
						>
							JOIN
						</button>
						<button
							onClick={() => setIsJoining(false)}
							className={`relative z-10 flex-1 py-2 text-sm font-bold transition-colors ${!isJoining ? 'text-black' : 'text-neutral-500'}`}
						>
							CREATE
						</button>
					</div>

					<motion.form
						layout // 🔑 Makes the form fields slide into place as height changes
						onSubmit={(e: any) => e.preventDefault()}
						className='space-y-4'
					>
						<AnimatePresence mode='popLayout'>
							{isJoining && (
								<motion.div
									initial={{ opacity: 0, x: -10 }}
									animate={{ opacity: 1, x: 0 }}
									exit={{ opacity: 0, x: -10 }}
								>
									<label className='block text-[10px] font-mono text-neutral-500 mb-1 ml-1 uppercase'>
										Session ID
									</label>
									<input
										type='text'
										placeholder='ALPHA-7'
										className='w-full bg-black/50 border border-white/5 rounded-xl p-3 outline-none focus:border-emerald-500/50'
									/>
								</motion.div>
							)}
						</AnimatePresence>

						<div>
							<label className='block text-[10px] font-mono text-neutral-500 mb-1 ml-1 uppercase'>
								Your Alias
							</label>
							<input
								type='text'
								placeholder='ghost_user'
								className='w-full bg-black/50 border border-white/5 rounded-xl p-3 outline-none focus:border-emerald-500/50'
							/>
						</div>

						<div>
							<label className='block text-[10px] font-mono text-neutral-500 mb-1 ml-1 uppercase'>
								Passcode
							</label>
							<input
								type='password'
								placeholder='••••••••'
								className='w-full bg-black/50 border border-white/5 rounded-xl p-3 outline-none focus:border-emerald-500/50'
							/>
						</div>

						<button className='w-full bg-emerald-500 text-black font-black py-4 rounded-xl mt-4 hover:scale-[1.02] active:scale-[0.98] transition-transform'>
							{isJoining ? 'ENTER SESSION' : 'INITIALIZE'}
						</button>
					</motion.form>
				</motion.div>
			</div>
		</AnimatePresence>
	);
};

export default AuthModal;
