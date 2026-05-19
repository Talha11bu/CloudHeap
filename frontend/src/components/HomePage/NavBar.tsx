import { useState } from 'react';
import { Menu, X, Terminal } from 'lucide-react';

const GithubIcon = () => (
	<svg
		viewBox='0 0 24 24'
		width='18'
		height='18'
		fill='none'
		stroke='currentColor'
		strokeWidth='2'
		strokeLinecap='round'
		strokeLinejoin='round'
	>
		<path d='M15 22v-4a4.8 4.8 0 0 0-1-3.5c3 0 6-2 6-5.5.08-1.25-.27-2.48-1-3.5.28-1.15.28-2.35 0-3.5 0 0-1 0-3 1.5-2.64-.5-5.36-.5-8 0C6 2 5 2 5 2c-.28 1.15-.28 2.35 0 3.5-.73 1.02-1.08 2.25-1 3.5 0 3.5 3 5.5 6 5.5-.39.49-.68 1.05-.85 1.65-.17.6-.22 1.23-.15 1.85v4' />
		<path d='M9 18c-4.51 2-5-2-7-2' />
	</svg>
);

const LinkedinIcon = () => (
	<svg
		viewBox='0 0 24 24'
		width='18'
		height='18'
		fill='none'
		stroke='currentColor'
		strokeWidth='2'
		strokeLinecap='round'
		strokeLinejoin='round'
	>
		<path d='M16 8a6 6 0 0 1 6 6v7h-4v-7a2 2 0 0 0-2-2 2 2 0 0 0-2 2v7h-4v-7a6 6 0 0 1 6-6z' />
		<rect width='4' height='12' x='2' y='9' />
		<circle cx='4' cy='4' r='2' />
	</svg>
);

const Navbar = () => {
	const [isOpen, setIsOpen] = useState(false);

	const navLinks = [
		{ name: 'About', href: '#about' },
		{ name: 'Features', href: '#features' },
		{ name: 'Stack', href: '#stack' },
		{ name: 'Privacy', href: '#footer' },
	];

	return (
		<nav className='w-full bg-black/50 backdrop-blur-md border-b border-white/5 sticky top-0 z-100'>
			<div className='max-w-7xl mx-auto px-6 h-16 flex justify-between items-center'>
				{/* Brand */}
				<div className='flex items-center gap-2 cursor-pointer group'>
					<div className='w-8 h-8 bg-emerald-500 rounded flex items-center justify-center'>
						<a href='/'>
							<Terminal size={18} className='text-black' />
						</a>
					</div>
					<span className='text-xl font-bold tracking-tighter text-white'>
						SILK ROAD
					</span>
				</div>

				{/* Desktop Links */}
				<div className='hidden md:flex items-center gap-8'>
					{navLinks.map((link) => (
						<a
							key={link.name}
							href={link.href}
							className='text-sm text-neutral-400 hover:text-emerald-400 transition-colors font-mono'
						>
							{link.name}
						</a>
					))}
					<div className='flex items-center gap-4 text-neutral-500'>
						<a
							href='https://github.com/your-username'
							target='_blank'
							rel='noreferrer'
							className='hover:text-emerald-500 transition-colors'
						>
							<GithubIcon />
						</a>
						<a
							href='https://linkedin.com/in/your-profile'
							target='_blank'
							rel='noreferrer'
							className='hover:text-emerald-500 transition-colors'
						>
							<LinkedinIcon />
						</a>
						<div className='w-px h-4 bg-white/10' />
						<span className='text-[10px] font-mono tracking-widest uppercase'>
							v1.0.0
						</span>
					</div>
				</div>

				{/* Burger Button */}
				<button
					onClick={() => setIsOpen(!isOpen)}
					className='md:hidden text-neutral-400'
				>
					{isOpen ? <X size={24} /> : <Menu size={24} />}
				</button>
			</div>

			{/* Mobile Menu (Push Down) */}
			<div
				className={`md:hidden overflow-hidden transition-all duration-300 ease-in-out bg-neutral-900 ${isOpen ? 'max-h-64' : 'max-h-0'}`}
			>
				<div className='p-6 flex flex-col gap-6'>
					{navLinks.map((link) => (
						<a
							key={link.name}
							href={link.href}
							onClick={() => setIsOpen(false)}
							className='text-neutral-300 text-lg font-mono'
						>
							{link.name}
						</a>
					))}
				</div>
			</div>
		</nav>
	);
};

export default Navbar;
