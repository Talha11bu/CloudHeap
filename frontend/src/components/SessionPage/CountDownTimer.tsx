import { useState, useEffect } from 'react';
import { Clock } from 'lucide-react';

interface CountdownTimerProps {
	expiresAt?: string; // e.g., "2026-05-06T20:30:00Z"
	durationStr?: string; // fallback if no expiresAt is found (e.g., "PT14M22S")
	onExpire: () => void;
}

const parseJavaDuration = (duration: string): number => {
	const match = duration.match(/PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+(?:\.\d+)?)S)?/);
	if (!match) return 0;
	const hours = parseInt(match[1] || '0', 10);
	const mins = parseInt(match[2] || '0', 10);
	const secs = parseFloat(match[3] || '0');
	return hours * 3600 + mins * 60 + Math.floor(secs);
};

export const CountdownTimer = ({ expiresAt, durationStr, onExpire }: CountdownTimerProps) => {
	
	// 🚀 FIX: Calculate initial time based on absolute timestamp if available
	const getInitialTime = () => {
		if (expiresAt) {
			const diffInSeconds = Math.floor((new Date(expiresAt).getTime() - Date.now()) / 1000);
			return Math.max(0, diffInSeconds);
		}
		if (durationStr) return parseJavaDuration(durationStr);
		return 0;
	};

	const [timeLeft, setTimeLeft] = useState(getInitialTime);

	useEffect(() => {
		if (timeLeft <= 0) {
			onExpire();
			return;
		}

		const interval = setInterval(() => {
			setTimeLeft((prev) => {
				// Resync with server expiration time every 10 ticks to prevent browser throttling drift
				if (expiresAt && prev % 10 === 0) {
					const diff = Math.floor((new Date(expiresAt).getTime() - Date.now()) / 1000);
					if (diff <= 0) {
						clearInterval(interval);
						onExpire();
						return 0;
					}
					return diff;
				}

				if (prev <= 1) {
					clearInterval(interval);
					onExpire();
					return 0;
				}
				return prev - 1;
			});
		}, 1000);

		return () => clearInterval(interval);
	}, [timeLeft, expiresAt, onExpire]);

	const formatTime = (totalSeconds: number) => {
		const h = Math.floor(totalSeconds / 3600);
		const m = Math.floor((totalSeconds % 3600) / 60);
		const s = totalSeconds % 60;
		if (h > 0) return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
		return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
	};

	const isLowTime = timeLeft < 60 && timeLeft > 0;

	return (
		<div className={`flex items-center gap-3 p-4 rounded-xl border transition-colors ${
			isLowTime ? 'bg-red-500/10 border-red-500/30 text-red-500 animate-pulse' : 'bg-white/5 border-white/10 text-emerald-500'
		}`}>
			<Clock size={20} className={isLowTime ? 'text-red-500' : 'text-emerald-500'} />
			<div>
				<p className="text-[10px] uppercase tracking-widest text-neutral-500 mb-1">Time Remaining</p>
				<p className="text-2xl font-black font-mono tracking-tighter leading-none">
					{formatTime(timeLeft)}
				</p>
			</div>
		</div>
	);
};