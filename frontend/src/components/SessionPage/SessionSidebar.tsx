import { Copy, CheckCircle2, X } from 'lucide-react';
import { useState } from 'react';
import { CountdownTimer } from './CountDownTimer';

interface SessionSidebarProps {
	sessionId: string;
	password: string | undefined;
	expiresAt: string | undefined;
	timeLeftStr: string | undefined;
	isHost: boolean;
	onExpire: () => void;
	onExit: () => void;
}

/** Left column sidebar: timer, session info cards with copy buttons, and exit button. */
export const SessionSidebar = ({
	sessionId,
	password,
	expiresAt,
	timeLeftStr,
	isHost,
	onExpire,
	onExit,
}: SessionSidebarProps) => {
	const [copiedField, setCopiedField] = useState<string | null>(null);

	const copyToClipboard = (text: string, field: string) => {
		navigator.clipboard.writeText(text);
		setCopiedField(field);
		setTimeout(() => setCopiedField(null), 2000);
	};

	return (
		<>
			{/* Timer & Exit Button Row */}
			<div className="flex items-center gap-3 mb-6 shrink-0">
				<CountdownTimer
					expiresAt={expiresAt}
					durationStr={timeLeftStr}
					onExpire={onExpire}
				/>

				<button
					onClick={onExit}
					className="flex-1 max-w-30 flex items-center justify-center gap-2 py-2 px-2 bg-red-500/10 border border-red-500/30 text-red-500 rounded-full text-xs font-bold uppercase tracking-widest hover:bg-red-500 hover:text-black transition-colors"
				>
					<X size={16} />
					{isHost ? 'End' : 'Leave'}
				</button>
			</div>

			{/* Session Info */}
			<div className="space-y-4 mb-8 shrink-0">
				<div>
					<p className="text-[10px] text-neutral-500 uppercase tracking-widest mb-1">
						Session ID
					</p>
					<div className="flex items-center justify-between bg-white/5 border border-white/10 rounded-lg p-3">
						<span className="font-mono text-sm truncate pr-2">
							{sessionId}
						</span>
						<button
							onClick={() => copyToClipboard(sessionId, 'id')}
							className="text-emerald-500 hover:text-emerald-400"
						>
							{copiedField === 'id' ? (
								<CheckCircle2 size={16} />
							) : (
								<Copy size={16} />
							)}
						</button>
					</div>
				</div>

				<div>
					<p className="text-[10px] text-neutral-500 uppercase tracking-widest mb-1">
						Access Key
					</p>
					<div className="flex items-center justify-between bg-white/5 border border-white/10 rounded-lg p-3">
						<span className="font-mono text-sm tracking-widest text-neutral-300 truncate pr-2">
							{password}
						</span>
						<button
							onClick={() => copyToClipboard(password || '', 'pass')}
							className="text-emerald-500 hover:text-emerald-400"
						>
							{copiedField === 'pass' ? (
								<CheckCircle2 size={16} />
							) : (
								<Copy size={16} />
							)}
						</button>
					</div>
				</div>
			</div>
		</>
	);
};
