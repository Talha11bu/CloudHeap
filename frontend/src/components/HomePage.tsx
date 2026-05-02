import { useState } from 'react';
import { ArrowRight } from 'lucide-react';
import { AuthModal } from './HomePage/AuthModal';
import { TechStack } from './HomePage/TechStack';
import { About } from './HomePage/About';
import { Features } from './HomePage/Features';
import { FooterSection } from './HomePage/FooterSection';

const HomePage = () => {
	const [showModal, setShowModal] = useState(false);

	return (
		<div className='relative min-h-screen'>
			<div className='fixed inset-0 -z-10 bg-[radial-gradient(#ffffff10_1px,transparent_1px)] bg-size-[40px_40px] mask-[radial-gradient(ellipse_50%_50%_at_50%_50%,#000_70%,transparent_100%)]' />

			<section className='max-w-5xl mx-auto px-6 pt-32 pb-16 text-center'>
				<div className='inline-flex items-center gap-3 px-4 py-2 rounded-full border border-emerald-500/10 bg-emerald-500/5 text-emerald-500 text-[11px] font-mono mb-10 uppercase tracking-[0.2em]'>
					<span className='relative flex h-2 w-2'>
						<span className='animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75'></span>
						<span className='relative inline-flex rounded-full h-2 w-2 bg-emerald-500'></span>
					</span>
					Protocol Active: v1.0
				</div>

				<h1 className='text-5xl md:text-8xl font-black tracking-tighter mb-8 leading-[0.9] text-white'>
					TRANSFER DATA <br />
					<span className='text-neutral-500 italic font-light'>
						WITHOUT A TRACE.
					</span>
				</h1>

				<p className='max-w-xl mx-auto text-neutral-400 text-base md:text-lg mb-12 font-mono leading-relaxed opacity-80'>
					Silk Road creates ephemeral, encrypted tunnels for file exchange. No
					storage logs. No persistence. Pure data flow.
				</p>

				<button
					onClick={() => setShowModal(true)}
					className='group relative px-10 py-5 bg-white text-black font-black rounded-full hover:bg-emerald-500 transition-all duration-300 flex items-center gap-3 mx-auto'
				>
					GET STARTED
					<ArrowRight
						size={18}
						className='group-hover:translate-x-1 transition-transform'
					/>
				</button>
			</section>

			<About />
			<Features />
			<TechStack />
			<FooterSection />
			{showModal && <AuthModal onClose={() => setShowModal(false)} />}
		</div>
	);
};

export default HomePage;
