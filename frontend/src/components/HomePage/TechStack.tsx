import { motion } from 'framer-motion';
import { ArrowUpRight } from 'lucide-react';

const Icons = {
	SpringBoot: () => (
		<svg
			viewBox='0 0 24 24'
			width='32'
			height='32'
			fill='none'
			stroke='currentColor'
			strokeWidth='1.5'
		>
			<path d='M12 2L3 7v10l9 5 9-5V7l-9-5z' />
			<path d='M12 22V12m0 0l9-5m-9 5L3 7' strokeOpacity='0.3' />
			<circle
				cx='12'
				cy='12'
				r='3'
				fill='currentColor'
				fillOpacity='0.1'
				stroke='currentColor'
			/>
		</svg>
	),
	Bun: () => (
		<svg
			viewBox='0 0 24 24'
			width='32'
			height='32'
			fill='none'
			stroke='currentColor'
			strokeWidth='1.5'
		>
			<path d='M12 3c-4.97 0-9 4.03-9 9s4.03 9 9 9 9-4.03 9-9-4.03-9-9-9z' />
			<path
				d='M8 12c0 2.21 1.79 4 4 4s4-1.79 4-4-1.79-4-4-4-4 1.79-4 4z'
				strokeOpacity='0.5'
			/>
			<path d='M12 8v4l3 2' />
		</svg>
	),
	React: () => (
		<svg
			viewBox='0 0 24 24'
			width='32'
			height='32'
			fill='none'
			stroke='currentColor'
			strokeWidth='1.5'
		>
			<circle cx='12' cy='12' r='2' />
			<path d='M12 12c5.523 0 10-2.239 10-5s-4.477-5-10-5-10 2.239-10 5 4.477 5 10 5z' />
			<path d='M12 12c5.523 0 10 2.239 10 5s-4.477 5-10 5-10-2.239-10-5 4.477-5 10-5z' />
			<path d='M2.5 12c0 2.761 4.254 5 9.5 5s9.5-2.239 9.5-5-4.254-5-9.5-5-9.5 2.239-9.5 5z' />
		</svg>
	),
	Cloudflare: () => (
		<svg
			viewBox='0 0 24 24'
			width='32'
			height='32'
			fill='none'
			stroke='currentColor'
			strokeWidth='1.5'
		>
			<path d='M17.5 19c.7 0 1.3-.2 1.8-.7.5-.5.7-1.1.7-1.8 0-1.2-.9-2.2-2.1-2.4-.3-1.9-1.9-3.4-3.9-3.4-.7 0-1.4.2-2 .6C11.3 9.8 9.8 9 8 9c-2.2 0-4 1.8-4 4 0 .2 0 .4.1.6C2.8 14.2 2 15.5 2 17c0 1.7 1.3 3 3 3h12.5z' />
		</svg>
	),
};

const STACK_DATA = [
	{
		name: 'Spring Boot 3',
		role: 'Core Infrastructure',
		desc: 'Enterprise-level safety and horizontal scalability. Years of industry trust repurposed for modern ephemeral data handling.',
		url: 'https://spring.io',
		icon: <Icons.SpringBoot />,
	},
	{
		name: 'Bun Runtime',
		role: 'High-Speed I/O',
		desc: 'A modern JavaScript runtime built for speed. Handling session logic with sub-millisecond response times.',
		url: 'https://bun.sh',
		icon: <Icons.Bun />,
	},
	{
		name: 'React 19 & Vite',
		role: 'Interface Engine',
		desc: 'Hyper-responsive UI architecture that maintains fluidity even during heavy file streaming operations.',
		url: 'https://vite.dev',
		icon: <Icons.React />,
	},
	{
		name: 'Cloudflare R2',
		role: 'Global Storage',
		desc: 'Edge-based object storage with zero egress fees. Fast, global, and perfectly suited for ephemeral file distribution.',
		url: 'https://cloudflare.com',
		icon: <Icons.Cloudflare />,
	},
];

export const TechStack = () => {
	return (
		<section id='stack' className='max-w-7xl mx-auto px-6 py-32 space-y-32'>
			<div className='text-center mb-24'>
				<h2 className='text-emerald-500 font-mono text-sm uppercase tracking-[0.3em] mb-4'>
					ARCHITECTURE
				</h2>
				<h3 className='text-4xl md:text-6xl font-black text-white tracking-tighter'>
					ENGINEERED FOR VELOCITY.
				</h3>
			</div>

			{STACK_DATA.map((tech, index) => {
				const isEven = index % 2 === 0;
				return (
					<div
						key={tech.name}
						className={`flex flex-col ${isEven ? 'md:flex-row' : 'md:flex-row-reverse'} items-center gap-12 lg:gap-24`}
					>
						{/* Card Half */}
						<motion.a
							href={tech.url}
							target='_blank'
							initial={{ opacity: 0, x: isEven ? -50 : 50 }}
							whileInView={{ opacity: 1, x: 0 }}
							viewport={{ once: true }}
							className='w-full md:w-1/2 group'
						>
							<div className='relative p-10 bg-neutral-900 border border-white/5 rounded-[2.5rem] overflow-hidden transition-all duration-500 group-hover:border-emerald-500/30 group-hover:bg-neutral-800/50'>
								<div className='text-emerald-500 mb-6 group-hover:scale-110 transition-transform duration-500'>
									{tech.icon}
								</div>
								<div className='flex justify-between items-end'>
									<div>
										<p className='text-neutral-500 font-mono text-[10px] uppercase tracking-widest mb-1'>
											{tech.role}
										</p>
										<h4 className='text-2xl font-black text-white'>
											{tech.name}
										</h4>
									</div>
									<ArrowUpRight
										className='text-neutral-700 group-hover:text-emerald-500 group-hover:translate-x-1 group-hover:-translate-y-1 transition-all'
										size={24}
									/>
								</div>
								{/* Decorative background element */}
								<div className='absolute -bottom-10 -right-10 w-32 h-32 bg-emerald-500/5 blur-3xl rounded-full group-hover:bg-emerald-500/10 transition-colors' />
							</div>
						</motion.a>

						{/* Text Half */}
						<motion.div
							initial={{ opacity: 0, x: isEven ? 50 : -50 }}
							whileInView={{ opacity: 1, x: 0 }}
							viewport={{ once: true }}
							className='w-full md:w-1/2 space-y-4 text-center md:text-left'
						>
							<h5 className='text-emerald-500/50 font-mono text-xs uppercase tracking-widest italic'>
								{tech.name} / Capabilities
							</h5>
							<p className='text-neutral-400 text-lg md:text-xl leading-relaxed font-medium'>
								{tech.desc}
							</p>
						</motion.div>
					</div>
				);
			})}
		</section>
	);
};
