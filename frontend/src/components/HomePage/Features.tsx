import { Shield, Lock, Zap } from 'lucide-react';

export const Features = () => {
	return (
		<section
			id='features'
			className='max-w-7xl mx-auto px-6 py-24 border-t border-white/5'
		>
			<div className='text-center mb-16'>
				<h2 className='text-3xl font-bold'>Engineered for Stealth</h2>
			</div>
			<div className='grid md:grid-cols-3 gap-6'>
				<FeatureCard
					icon={<Shield />}
					title='End-to-End'
					desc='Your files are encrypted before they even leave your browser.'
				/>
				<FeatureCard
					icon={<Lock />}
					title='Self-Destruct'
					desc='Links expire automatically after a set time or single download.'
				/>
				<FeatureCard
					icon={<Zap />}
					title='Stream Only'
					desc='Data flows through the server memory—never the hard drive.'
				/>
			</div>
		</section>
	);
};
const FeatureCard = ({ icon, title, desc }: any) => (
	<div className='p-8 bg-white/2 border border-white/5 rounded-3xl hover:bg-white/4 hover:border-emerald-500/20 transition-all group'>
		<div className='text-emerald-500 mb-4 group-hover:scale-110 transition-transform'>
			{icon}
		</div>
		<h3 className='text-lg font-bold text-white mb-2'>{title}</h3>
		<p className='text-sm text-neutral-500 leading-relaxed'>{desc}</p>
	</div>
);
