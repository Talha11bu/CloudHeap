import { motion } from 'framer-motion';

export const About = () => {
	return (
		<section
			id='about'
			className='min-h-screen max-w-7xl mx-auto px-6 py-32 border-t border-white/5 flex flex-col justify-center'
		>
			<div className='grid lg:grid-cols-2 gap-20 items-start'>
				{/* Left Column: The Narrative */}
				<div className='space-y-12'>
					<div>
						<h2 className='text-emerald-500 font-mono text-sm uppercase tracking-[0.3em] mb-4'>
							THE GENESIS
						</h2>
						<h3 className='text-5xl font-black text-white leading-tight tracking-tighter'>
							EXISTING METHODS ARE <br />
							<span className='text-neutral-500 italic'>BROKEN BY DESIGN.</span>
						</h3>
					</div>

					<div className='space-y-8'>
						<p className='text-neutral-400 text-lg leading-relaxed'>
							Sharing a file shouldn't require a background check. Most
							platforms today demand an
							<span className='text-white'> Identity Tax</span> − you need an
							account, a phone number, or an email address just to move data
							from point A to point B.
						</p>

						<div className='grid gap-6'>
							<div className='p-6 bg-white/2 border-l-2 border-red-500/50'>
								<h4 className='text-white font-bold text-sm mb-2 uppercase'>
									The Range & Speed Trap
								</h4>
								<p className='text-neutral-500 text-sm font-mono'>
									Bluetooth is tethered by physical distance and archaic speed
									caps. If you aren't in the same room, you're out of luck. If
									the file is large, you're waiting for hours.
								</p>
							</div>

							<div className='p-6 bg-white/2 border-l-2 border-red-500/50'>
								<h4 className='text-white font-bold text-sm mb-2 uppercase'>
									The Identity Lock-in
								</h4>
								<p className='text-neutral-500 text-sm font-mono'>
									Email, Messaging apps, and Cloud drives require you to be
									"known" to the platform. To share, you must expose your
									profile. Silk Road deletes the middleman.
								</p>
							</div>
						</div>
					</div>
				</div>

				{/* Right Column: The Silk Road Solution */}
				<div className='relative pt-12 lg:pt-0'>
					<div className='sticky top-32 space-y-8 p-8 bg-neutral-900/50 border border-white/5 rounded-3xl backdrop-blur-xl'>
						<h4 className='text-emerald-500 font-black text-2xl tracking-tighter italic'>
							THE SILK ROAD WAY
						</h4>

						<ul className='space-y-6'>
							<li className='flex gap-4'>
								<div className='h-6 w-6 rounded-full bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-emerald-500 text-[10px] font-bold'>
									01
								</div>
								<div>
									<p className='text-white font-bold text-sm'>
										TOTAL ANONYMITY
									</p>
									<p className='text-neutral-500 text-xs mt-1'>
										No accounts. No identities. Just a session ID and a
										handshake.
									</p>
								</div>
							</li>
							<li className='flex gap-4'>
								<div className='h-6 w-6 rounded-full bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-emerald-500 text-[10px] font-bold'>
									02
								</div>
								<div>
									<p className='text-white font-bold text-sm'>
										UNBOUND VELOCITY
									</p>
									<p className='text-neutral-500 text-xs mt-1'>
										We don't throttle. Your transfer speed is limited only by
										your ISP and connectivity.
									</p>
								</div>
							</li>
							<li className='flex gap-4'>
								<div className='h-6 w-6 rounded-full bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-emerald-500 text-[10px] font-bold'>
									03
								</div>
								<div>
									<p className='text-white font-bold text-sm'>GLOBAL REACH</p>
									<p className='text-neutral-500 text-xs mt-1'>
										Share with someone across the room or across the planet with
										the same level of ease.
									</p>
								</div>
							</li>
						</ul>

						{/* Subtle decorative visualizer*/}
						<div className='pt-6 border-t border-white/5 flex items-end gap-1 h-12'>
							{[40, 70, 45, 90, 65, 80, 30, 95, 50].map((h, i) => (
								<motion.div
									key={i}
									initial={{ height: 0 }}
									animate={{ height: `${h}%` }}
									transition={{
										repeat: Infinity,
										duration: 1,
										repeatType: 'reverse',
										delay: i * 0.1,
									}}
									className='flex-1 bg-emerald-500/20 rounded-t-sm'
								/>
							))}
						</div>
					</div>
				</div>
			</div>
		</section>
	);
};
