import { useState, useEffect, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Terminal, Users, X, AlertTriangle, Copy, CheckCircle2, ShieldAlert } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { CountdownTimer } from './SessionPage/CountDownTimer'; 
import { FilePanel } from './SessionPage/FilePanel';
import { Client } from '@stomp/stompjs'; 

const WS_BASE = 'ws://localhost:8080/ws';
const API_BASE = 'http://localhost:8080';

interface SessionData {
	sucess?: boolean;
	success?: boolean;
	Token?: string;
	session: {
		sessionId: string;
		password?: string;
		expiresAt?: string;
		users: string[];
		files: string[];
	};
	timeLeft: string;
	duration: string;
	clientUsername: string; 
}

export const SessionPage = () => {
	const location = useLocation();
	const navigate = useNavigate();
	
	const [sessionData, setSessionData] = useState<SessionData | null>(null);
	
	const [isErrorModalOpen, setIsErrorModalOpen] = useState(false);
	const [isExpiredModalOpen, setIsExpiredModalOpen] = useState(false);
	const [isMobileDrawerOpen, setIsMobileDrawerOpen] = useState(false);
	const [isExitModalOpen, setIsExitModalOpen] = useState(false);
	
	const [users, setUsers] = useState<string[]>([]);
	const [files, setFiles] = useState<string[]>([]);
	const [copiedField, setCopiedField] = useState<string | null>(null);

	const stompClient = useRef<Client | null>(null);

	useEffect(() => {
		const data = location.state?.sessionData;
		
		if (!data || (data.sucess === false && data.success === false)) {
			setIsErrorModalOpen(true);
			return;
		}

		setSessionData(data);
		setUsers(data.session.users || []); 
		setFiles(data.session.files || []);
		
	}, [location.state]);

	// 2. WebSocket Connection Effect
	useEffect(() => {
		// Only try to connect if we have successfully loaded the sessionData
		if (!sessionData?.session?.sessionId) return;

		const sessionId = sessionData.session.sessionId;

		const token = localStorage.getItem('silk_road_jwt');

		const client = new Client({
			brokerURL: `${WS_BASE}?token=${token}`,
			reconnectDelay: 5000,
			onConnect: () => {
				console.log("WebSocket Connected!");
				
				client.subscribe(`/topic/session/${sessionId}`, (message) => {
					
					// Check for raw text termination message
					
					if (message.body.includes("successfully ended")) {
						setIsExpiredModalOpen(true);
						return;
					}

					try {
						const data = JSON.parse(message.body);
						
						if (data.type === 'USER_JOINED') {
							setUsers(prev => [...new Set([...prev, data.payload])]);
						} 
						else if (data.type === 'USER_LEFT') {
							setUsers(prev => prev.filter(u => u !== data.payload));
						}
						else if (data.type === 'FILE_UPLOADED') {
							setFiles(prev => [...new Set([...prev, data.payload])]);
						} else if (data.type === 'FILE_DELETED') {
							setFiles(prev => prev.filter(f => f !== data.payload));
						}
						
					} catch (e) {
						console.error("Failed to parse WebSocket message:", message.body);
					}
				});
			},
			onWebSocketError: (error) => {
				console.error("WebSocket Error:", error);
			}
		});

		client.activate();
		stompClient.current = client;

		// Cleanup function: Disconnect WebSocket when the user leaves the page
		return () => {
			if (stompClient.current) {
				stompClient.current.deactivate();
			}
		};
	}, [sessionData?.session?.sessionId]);

	const copyToClipboard = (text: string, field: string) => {
		navigator.clipboard.writeText(text);
		setCopiedField(field);
		setTimeout(() => setCopiedField(null), 2000);
	};

	const returnToBase = () => {
		localStorage.removeItem('silk_road_jwt');
		if (stompClient.current) stompClient.current.deactivate();
		navigate('/');
	};

	const isHost = sessionData?.clientUsername === sessionData?.session?.users?.[0];

	// Handles routing to the correct Spring Boot endpoint
	const handleExitSession = async () => {
		if (!sessionData) return;
		
		const sessionId = sessionData.session.sessionId;
		const username = sessionData.clientUsername;
		
		try {
			// Host triggers the full session purge, Joiner just leaves
			const endpoint = isHost 
				? `${API_BASE}/sessions/${sessionId}?username=${username}`
				: `${API_BASE}/sessions/${sessionId}/leave?username=${username}`;

			await fetch(endpoint, { method: 'DELETE' });
			
		} catch (error) {
			console.error("Error communicating exit to server:", error);
		} finally {
			// Always clean up locally even if the server request drops
			returnToBase();
		}
	};

	// --- MODALS ---
	if (isErrorModalOpen) {
		return (
			<div className="min-h-screen bg-[#050505] flex items-center justify-center p-4">
				<div className="bg-neutral-900 border border-white/10 p-8 rounded-2xl max-w-sm w-full text-center space-y-6">
					<AlertTriangle className="text-red-500 mx-auto" size={48} />
					<div>
						<h2 className="text-white font-black uppercase tracking-widest text-lg">System Failure</h2>
						<p className="text-neutral-400 font-mono text-xs mt-2">Something went wrong establishing the session. Please verify your credentials and try again.</p>
					</div>
					<button onClick={() => navigate('/')} className="w-full py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono text-xs hover:bg-white/10 transition-colors uppercase tracking-widest">
						Return to Base
					</button>
				</div>
			</div>
		);
	}

	if (isExpiredModalOpen) {
		return (
			<div className="min-h-screen bg-[#050505] flex items-center justify-center p-4">
				<div className="bg-neutral-900 border border-white/10 p-8 rounded-2xl max-w-sm w-full text-center space-y-6">
					<ShieldAlert className="text-yellow-500 mx-auto" size={48} />
					<div>
						<h2 className="text-white font-black uppercase tracking-widest text-lg">Session Expired</h2>
						<p className="text-neutral-400 font-mono text-xs mt-2">The Time-to-Live (TTL) for this tunnel has reached zero. All data has been purged.</p>
					</div>
					<button onClick={returnToBase} className="w-full py-3 bg-emerald-500/10 border border-emerald-500/30 rounded-lg text-emerald-500 font-bold font-mono text-xs hover:bg-emerald-500 hover:text-black transition-colors uppercase tracking-widest">
						Acknowledge & Exit
					</button>
				</div>
			</div>
		);
	}

	// Wait for data to load
	if (!sessionData) return null;

	// --- MAIN UI RENDER ---
	return (
		<div className="min-h-screen md:h-screen bg-[#050505] text-white flex flex-col md:flex-row overflow-x-hidden">

			<AnimatePresence>
				{isExitModalOpen && (
					<motion.div 
						initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
						className="absolute inset-0 z-100 bg-black/5 backdrop-blur-sm flex items-center justify-center p-4"
					>
						<motion.div 
							initial={{ scale: 0.95, y: 10 }} animate={{ scale: 1, y: 0 }} exit={{ scale: 0.95, y: 10 }}
							className="bg-neutral-900 border border-white/10 p-8 rounded-2xl max-w-sm w-full text-center space-y-6 shadow-2xl"
						>
							<AlertTriangle className="text-red-500 mx-auto" size={48} />
							<div>
								<h2 className="text-white font-black uppercase tracking-widest text-lg">
									{isHost ? 'Terminate Session?' : 'Disconnect?'}
								</h2>
								<p className="text-neutral-400 font-mono text-xs mt-2 leading-relaxed">
									{isHost 
										? 'WARNING: This will immediately collapse the tunnel and permanently purge all files for all connected agents.' 
										: 'You are about to disconnect from the secure tunnel. You will need the Access Key to rejoin.'}
								</p>
							</div>
							<div className="flex gap-4">
								<button 
									onClick={() => setIsExitModalOpen(false)}
									className="flex-1 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono text-xs hover:bg-white/10 transition-colors uppercase tracking-widest"
								>
									Cancel
								</button>
								<button 
									onClick={handleExitSession} 
									className="flex-1 py-3 bg-red-500/10 border border-red-500/30 rounded-lg text-red-500 font-bold font-mono text-xs hover:bg-red-500 hover:text-black transition-colors uppercase tracking-widest"
								>
									{isHost ? 'Terminate' : 'Leave'}
								</button>
							</div>
						</motion.div>
					</motion.div>
				)}
			</AnimatePresence>
			
			{/* MOBILE HEADER: Logo & Drawer Toggle */}
			<div className="md:hidden flex items-center justify-between p-4 border-b border-white/10 bg-black/50 backdrop-blur-md z-20">
				<div className="flex items-center gap-2">
					<Terminal size={20} className="text-emerald-500" />
					<span className="font-bold tracking-widest text-sm uppercase">Silk Road</span>
				</div>
				<button onClick={() => setIsMobileDrawerOpen(true)} className="p-2 bg-white/5 rounded-lg text-white">
					<Users size={20} />
				</button>
			</div>

			{/* LEFT COLUMN (Desktop) / TOP CONTENT (Mobile) */}
			<div className="w-full md:w-80 border-b md:border-b-0 md:border-r border-white/10 bg-black/30 flex flex-col p-6 overflow-y-auto">
				
				{/* Desktop Only Header */}
				<div className="hidden md:flex items-center gap-2 mb-8 shrink-0">
					<Terminal size={24} className="text-emerald-500" />
					<span className="font-bold tracking-widest text-lg uppercase">Silk Road</span>
				</div>

				{/* Timer & Exit Button Row */}
				<div className="flex items-center gap-3 mb-6 shrink-0">
					<CountdownTimer 
						expiresAt={sessionData.session?.expiresAt} 
						durationStr={sessionData.timeLeft || sessionData.duration} 
						onExpire={() => setIsExpiredModalOpen(true)} 
					/>
					
					{/* UI FIX: flex-1 ensures it only takes remaining space next to the timer, max-w-[120px] keeps it from looking absurdly long */}
					<button 
						onClick={() => setIsExitModalOpen(true)}
						className="flex-1 max-w-30 flex items-center justify-center gap-2 py-2 px-2 bg-red-500/10 border border-red-500/30 text-red-500 rounded-full text-xs font-bold uppercase tracking-widest hover:bg-red-500 hover:text-black transition-colors"
					>
						<X size={16} />
						{isHost ? 'End' : 'Leave'}
					</button>
				</div>

				{/* Session Info */}
				<div className="space-y-4 mb-8 shrink-0">
					<div>
						<p className="text-[10px] text-neutral-500 uppercase tracking-widest mb-1">Session ID</p>
						<div className="flex items-center justify-between bg-white/5 border border-white/10 rounded-lg p-3">
							<span className="font-mono text-sm truncate pr-2">{sessionData.session.sessionId}</span>
							<button onClick={() => copyToClipboard(sessionData.session.sessionId, 'id')} className="text-emerald-500 hover:text-emerald-400">
								{copiedField === 'id' ? <CheckCircle2 size={16} /> : <Copy size={16} />}
							</button>
						</div>
					</div>
					
					<div>
						<p className="text-[10px] text-neutral-500 uppercase tracking-widest mb-1">Access Key</p>
						<div className="flex items-center justify-between bg-white/5 border border-white/10 rounded-lg p-3">
							<span className="font-mono text-sm tracking-widest text-neutral-300 truncate pr-2">{sessionData.session.password}</span>
							<button onClick={() => copyToClipboard(sessionData.session.password || '', 'pass')} className="text-emerald-500 hover:text-emerald-400">
								{copiedField === 'pass' ? <CheckCircle2 size={16} /> : <Copy size={16} />}
							</button>
						</div>
					</div>

					</div>

				{/* Desktop Users List (Hidden on Mobile) */}
				<div className="hidden md:flex flex-col flex-1 min-h-0">
					<p className="text-[10px] text-neutral-500 uppercase tracking-widest mb-4 border-b border-white/10 pb-2 flex items-center justify-between">
						<span>Connected Agents</span>
						<span className="bg-emerald-500/20 text-emerald-500 px-2 py-0.5 rounded text-[10px]">{users.length}</span>
					</p>
					<ul className="space-y-3 overflow-y-auto">
						{users.map((user, idx) => (
							<li key={idx} className="flex items-center gap-3 text-sm text-neutral-300 font-mono">
								<span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
								{user} {user === sessionData.clientUsername && <span className="text-emerald-500/50 text-[10px]">(You)</span>}
							</li>
						))}
					</ul>
				</div>
			</div>

			{/* RIGHT COLUMN: Upload Area */}
			<div className="flex-1 w-full md:h-screen md:overflow-y-auto bg-[#050505] flex flex-col">
				<FilePanel 
					sessionId={sessionData.session.sessionId}
					password={sessionData.session.password}
					files={files}
				/>
			</div>

			{/* MOBILE DRAWER: Discord Style Slide-in */}
			<AnimatePresence>
				{isMobileDrawerOpen && (
					<>
						{/* Backdrop */}
						<motion.div 
							initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
							onClick={() => setIsMobileDrawerOpen(false)}
							className="md:hidden fixed inset-0 bg-black/80 backdrop-blur-sm z-40"
						/>
						
						{/* Sliding Menu */}
						<motion.div 
							initial={{ x: '100%' }} animate={{ x: 0 }} exit={{ x: '100%' }}
							transition={{ type: "spring", damping: 25, stiffness: 200 }}
							className="md:hidden fixed inset-y-0 right-0 w-64 bg-neutral-950 border-l border-white/10 z-50 p-6 flex flex-col shadow-2xl"
						>
							<div className="flex items-center justify-between border-b border-white/10 pb-4 mb-4">
								<h2 className="text-xs font-bold uppercase tracking-widest text-emerald-500 flex items-center gap-2">
									<Users size={16} /> Agents ({users.length})
								</h2>
								<button onClick={() => setIsMobileDrawerOpen(false)} className="p-1 hover:bg-white/10 rounded-md">
									<X size={20} className="text-neutral-400" />
								</button>
							</div>

							<ul className="space-y-4 overflow-y-auto">
								{users.map((user, idx) => (
									<li key={idx} className="flex items-center gap-3 text-sm text-neutral-300 font-mono">
										<span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
										{user} {user === sessionData.clientUsername && <span className="text-emerald-500/50 text-[10px]">(You)</span>}
									</li>
								))}
							</ul>
						</motion.div>
					</>
				)}
			</AnimatePresence>

		</div>
	);
};