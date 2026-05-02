import React, { useRef } from 'react';

const TimerDial = ({
	value,
	onChange,
}: {
	value: number;
	onChange: (v: number) => void;
}) => {
	const audioCtx = useRef<AudioContext | null>(null);
	const isTickRef = useRef(true);

	const playTickTock = () => {
		if (!audioCtx.current) audioCtx.current = new AudioContext();
		const t = audioCtx.current.currentTime;

		const osc = audioCtx.current.createOscillator();
		const gain = audioCtx.current.createGain();

		const isTick = isTickRef.current;
		isTickRef.current = !isTick;

		osc.type = 'triangle';

		const startFreq = isTick ? 1200 : 900;
		const endFreq = isTick ? 100 : 80;

		osc.frequency.setValueAtTime(startFreq, t);
		osc.frequency.exponentialRampToValueAtTime(endFreq, t + 0.03);

		// Very sharp volume envelope for a crisp click
		gain.gain.setValueAtTime(0.5, t);
		gain.gain.exponentialRampToValueAtTime(0.001, t + 0.03);

		osc.connect(gain);
		gain.connect(audioCtx.current.destination);

		osc.start(t);
		osc.stop(t + 0.04);
	};

	const handleWheel = (e: React.WheelEvent) => {
		const direction = e.deltaY > 0 ? -5 : 5;
		const nextValue = Math.max(10, Math.min(60, value + direction));

		if (nextValue !== value) {
			playTickTock();
			onChange(nextValue);
		}
	};

	const handleCenterClick = () => {
		const nextValue = value >= 60 ? 5 : value + 5;
		playTickTock();
		onChange(nextValue);
	};

	return (
		<div className='flex flex-col items-center gap-6 p-4'>
			<div className='relative group' onWheel={handleWheel}>
				<svg
					width='200'
					height='200'
					viewBox='0 0 100 100'
					className='select-none'
				>
					{/* Clock Marks */}
					{[...Array(12)].map((_, i) => (
						<line
							key={i}
							x1='50'
							y1='5'
							x2='50'
							y2='10'
							transform={`rotate(${i * 30}, 50, 50)`}
							// We leave the color transition here so the lights fade smoothly, but the rotation snaps
							className={`${(i + 1) * 5 <= value ? 'stroke-emerald-500' : 'stroke-neutral-800'} transition-colors duration-300`}
							strokeWidth='1'
						/>
					))}

					{/* Active Progress Arc */}
					<circle
						cx='50'
						cy='50'
						r='40'
						fill='none'
						stroke='currentColor'
						strokeWidth='2'
						className='text-emerald-500/20'
					/>
					<circle
						cx='50'
						cy='50'
						r='40'
						fill='none'
						stroke='currentColor'
						strokeWidth='2'
						strokeDasharray={`${(value / 60) * 251.2} 251.2`}
						transform='rotate(-90, 50, 50)'
						className='text-emerald-500'
						// 🚀 FIX: Removed transition classes from the arc so it instantly snaps
					/>

					{/* The Knob Indicator */}
					<g transform={`rotate(${(value / 60) * 360}, 50, 50)`}>
						{/* 🚀 FIX: Removed transition classes from the group so it instantly snaps */}
						<circle
							cx='50'
							cy='50'
							r='30'
							className='fill-neutral-900 stroke-white/10'
						/>
						<circle
							cx='50'
							cy='25'
							r='3'
							className='fill-emerald-500 shadow-glow'
						/>
					</g>
				</svg>

				{/* Digital Display / Click Target */}
				<button
					type='button'
					onClick={handleCenterClick}
					className='absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-24 h-24 rounded-full flex flex-col items-center justify-center hover:bg-white/5 transition-colors cursor-pointer outline-none'
					aria-label='Increase time by 5 minutes'
				>
					<span className='text-3xl font-black text-white block'>{value}</span>
					<span className='text-[10px] font-mono text-emerald-500 uppercase tracking-widest'>
						MINS
					</span>
				</button>
			</div>
		</div>
	);
};

export default TimerDial;
