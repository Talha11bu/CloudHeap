import { useState } from 'react';
import { motion } from 'framer-motion';
import { Shield, Lock, AlertCircle } from 'lucide-react';

export const FooterSection = () => {
	const [isFlipped, setIsFlipped] = useState(false);

	return (
		<footer
			id='footer'
			className='relative mt-20 border-t border-emerald-500/40 bg-black overflow-hidden shadow-[0_-25px_50px_-12px_rgba(16,185,129,0.15)]'
		>
			<div className='absolute inset-0 bg-[radial-gradient(circle_at_bottom_right,rgba(16,185,129,0.2)_0%,transparent_60%)]' />
			<div className='absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(16,185,129,0.12)_0%,transparent_50%)]' />
			{/*High-Contrast Glow Background */}
			<div className='relative max-w-7xl mx-auto px-6 py-24'>
				<div className='grid lg:grid-cols-2 gap-16 items-center'>
					{/* LEFT: Privacy & Security */}
					<div className='space-y-10'>
						<div>
							<h2 className='text-red-500 font-mono text-xs uppercase tracking-[0.4em] mb-4'>
								// LEGAL HANDSHAKE
							</h2>
							<h3 className='text-4xl font-black text-white tracking-tighter'>
								PRIVACY <span className='text-neutral-500'>&</span> TERMS
							</h3>
						</div>

						<div className='grid gap-6'>
							<div className='flex gap-4 group'>
								<div className='mt-1 text-emerald-500 group-hover:scale-110 transition-transform'>
									<Shield size={20} />
								</div>
								<div>
									<h4 className='text-white font-bold text-sm uppercase mb-1'>
										Zero Persistence
									</h4>
									<p className='text-neutral-500 text-xs leading-relaxed font-mono'>
										Data is piped, not stored. Once a session terminates or the
										timer hits zero, the tunnel collapses and all metadata is
										purged.
									</p>
								</div>
							</div>

							<div className='flex gap-4 group'>
								<div className='mt-1 text-red-500 group-hover:scale-110 transition-transform'>
									<AlertCircle size={20} />
								</div>
								<div>
									<h4 className='text-white font-bold text-sm uppercase mb-1'>
										Ops-Sec Warning
									</h4>
									<p className='text-neutral-500 text-xs leading-relaxed font-mono'>
										Do not accept session IDs from untrusted sources. Silk Road
										provides the medium; you provide the discretion.
									</p>
								</div>
							</div>

							<div className='flex gap-4 group'>
								<div className='mt-1 text-emerald-500 group-hover:scale-110 transition-transform'>
									<Lock size={20} />
								</div>
								<div>
									<h4 className='text-white font-bold text-sm uppercase mb-1'>
										Point-to-Point
									</h4>
									<p className='text-neutral-500 text-xs leading-relaxed font-mono'>
										Encrypted handshakes ensure that even we cannot see what
										flows through the Silk Road.
									</p>
								</div>
							</div>
						</div>
					</div>

					{/* RIGHT: Creator Card */}
					<div className='flex flex-col items-center lg:items-end justify-center'>
						<div
							className='relative w-80 h-48 perspective-1000 cursor-pointer'
							onClick={() => setIsFlipped(!isFlipped)}
						>
							<motion.div
								animate={{ rotateY: isFlipped ? 180 : 0 }}
								transition={{
									duration: 0.6,
									type: 'spring',
									stiffness: 260,
									damping: 20,
								}}
								style={{ transformStyle: 'preserve-3d' }}
								className='w-full h-full'
							>
								{/* Front: Creator Info */}
								<div className='absolute inset-0 backface-hidden bg-neutral-900 border border-white/10 rounded-3xl p-8 flex flex-col justify-between shadow-2xl'>
									<div className='flex justify-between items-start'>
										<div>
											<p className='text-[10px] font-mono text-emerald-500 uppercase tracking-[0.3em] mb-1'>
												Architect
											</p>
											<h4 className='text-2xl font-black text-white tracking-tighter uppercase'>
												Abu Talha
											</h4>
										</div>
										<div className='h-10 w-10 rounded-full bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center'>
											<div className='h-2 w-2 rounded-full bg-emerald-500 animate-pulse' />
										</div>
									</div>
									<div className='flex justify-between items-center'>
										<p className='text-[9px] text-neutral-500 font-mono uppercase tracking-widest'>
											Full-Stack Dev
										</p>
										<span className='text-[10px] text-emerald-500 font-bold tracking-tighter'>
											CLICK TO FLIP
										</span>
									</div>
								</div>

								{/* Back: Donation Links */}
								<div
									className='absolute inset-0 backface-hidden bg-emerald-500 rounded-3xl p-8 flex flex-col justify-between shadow-[0_0_50px_rgba(16,185,129,0.3)]'
									style={{ transform: 'rotateY(180deg)' }}
								>
									<div>
										<h4 className='text-black font-black text-xl tracking-tighter uppercase'>
											Support The Project
										</h4>
										<p className='text-black/60 text-[10px] font-mono mt-1 font-bold uppercase'>
											Help keep the servers running.
										</p>
									</div>
									<div className='space-y-2'>
										{['Buy me a coffee', 'Crypto / Wallet', 'PayPal'].map(
											(link) => (
												<a
													key={link}
													href='#'
													className='flex justify-between items-center text-[11px] font-black text-black border-b border-black/10 pb-1 hover:border-black transition-colors'
												>
													{link.toUpperCase()}
													<div className='h-1 w-1 bg-black rounded-full' />
												</a>
											),
										)}
									</div>
								</div>
							</motion.div>
						</div>

						<p className='mt-8 text-[10px] font-mono text-neutral-600 uppercase tracking-[0.5em] lg:mr-4'>
							© 2026 SILK ROAD PROTOCOL
						</p>
					</div>
				</div>
			</div>
		</footer>
	);
};
