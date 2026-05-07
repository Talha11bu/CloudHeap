import { useState, useRef } from 'react';
import { Upload, Search, Trash2, File as FileIcon, AlertTriangle, Loader2, Plus, Download, Archive } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const API_BASE = 'http://localhost:8080';

interface FilePanelProps {
	sessionId: string;
	password?: string;
	files: string[];
}

export const FilePanel = ({ sessionId, password, files }: FilePanelProps) => {
	const [searchQuery, setSearchQuery] = useState('');
	const [isUploading, setIsUploading] = useState(false);
	const [fileToDelete, setFileToDelete] = useState<string | null>(null);
	
	const fileInputRef = useRef<HTMLInputElement>(null);

	// Filter files based on search
	const filteredFiles = files.filter(f => 
		f.toLowerCase().includes(searchQuery.toLowerCase())
	);

	const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
		const selectedFile = e.target.files?.[0];
		if (!selectedFile) return;

		setIsUploading(true);
		const formData = new FormData();
		formData.append('file', selectedFile);
		formData.append('sessionId', sessionId);
		if (password) formData.append('password', password);

		try {
			// Adjust spelling if your backend strictly requires 'uplaod'
			await fetch(`${API_BASE}/sessions/${sessionId}/upload`, {
				method: 'POST',
				body: formData
			});
			// Success! The WebSocket will broadcast the new file to everyone
		} catch (error) {
			console.error("Upload failed", error);
		} finally {
			setIsUploading(false);
			if (fileInputRef.current) fileInputRef.current.value = '';
		}
	};

	const handleDelete = async () => {
		if (!fileToDelete) return;
		try {
			await fetch(`${API_BASE}/sessions/${sessionId}/file?fileName=${encodeURIComponent(fileToDelete)}`, {
				method: 'DELETE'
			});
			// Success! The WebSocket will broadcast the deletion to everyone
		} catch (error) {
			console.error("Delete failed", error);
		} finally {
			setFileToDelete(null);
		}
	};

	const handleDownload = async (fileName: string) => {
		try {
			const token = localStorage.getItem('silk_road_jwt');
			
			const res = await fetch(`${API_BASE}/sessions/${sessionId}/files/${encodeURIComponent(fileName)}/download-url`, {
				method: 'GET',
				headers: {
					'Authorization': `Bearer ${token}`
				}
			});

			if (!res.ok) throw new Error(`Server returned ${res.status}`);

			const data = await res.json();
			
			if (data.url) {
				const link = document.createElement('a');
				link.href = data.url;
				
				document.body.appendChild(link);
				link.click();
				document.body.removeChild(link);
			}
			
		} catch (error) {
			console.error("Download failed", error);
		}
	};

	const handleDownloadAll = async () => {
		try {
			const token = localStorage.getItem('silk_road_jwt');
			
			const url = new URL(`${API_BASE}/sessions/${sessionId}/files/zip`);
			if (password) {
				url.searchParams.append('password', password);
			}

			const res = await fetch(url.toString(), {
				method: 'GET',
				headers: { 'Authorization': `Bearer ${token}` }
			});

			if (!res.ok) throw new Error("Failed to generate zip archive");

			const blob = await res.blob();
			const blobUrl = window.URL.createObjectURL(blob);
			const link = document.createElement('a');
			link.href = blobUrl;
			link.download = `CloudShare_${sessionId}_Archive.zip`;
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
			window.URL.revokeObjectURL(blobUrl);
			
		} catch (error) {
			console.error("Zip download failed", error);
		}
	};
	return (
		<div className="flex flex-col w-full min-h-full p-4 md:p-8 bg-[radial-gradient(circle_at_center,rgba(16,185,129,0.03)_0%,transparent_60%)]">
			
			{/* HIDDEN FILE INPUT */}
			<input type="file" className="hidden" ref={fileInputRef} onChange={handleUpload} />

			{/* UI FIX: max-w-5xl gives it plenty of room on desktop without looking stretched on ultrawides */}
			<div className="w-full max-w-5xl mx-auto flex flex-col">

				{/* ROW 1: Search & Desktop Upload */}
				<div className="flex flex-col md:flex-row items-stretch md:items-center gap-4 mb-4 w-full">
					
					{/* Search Bar - UI FIX: 'flex-1' makes it span all available width! */}
					<div className="relative flex-1 w-full">
						<Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-neutral-500" />
						<input 
							type="text" 
							placeholder="SEARCH FILES..." 
							value={searchQuery}
							onChange={(e) => setSearchQuery(e.target.value)}
							className="w-full bg-black/50 border border-white/10 rounded-xl pl-10 pr-4 py-3 text-xs text-white font-mono uppercase tracking-widest outline-none focus:border-emerald-500/50 transition-colors placeholder:text-neutral-600"
						/>
					</div>

					{/* Desktop Upload Button - UI FIX: 'shrink-0' stops it from being squished by the search bar */}
					<button 
						onClick={() => fileInputRef.current?.click()}
						disabled={isUploading}
						className="hidden md:flex shrink-0 items-center justify-center gap-2 bg-emerald-500 text-black px-6 py-3 rounded-xl font-bold text-xs uppercase tracking-widest hover:bg-emerald-400 transition-all disabled:opacity-50 shadow-[0_0_15px_rgba(16,185,129,0.2)]"
					>
						{isUploading ? <Loader2 size={16} className="animate-spin" /> : <Upload size={16} />}
						Upload File
					</button>
				</div>

				{/* ROW 2: Download All (ZIP) Button */}
				{files.length > 0 && (
					<div className="flex justify-start mb-6 w-full">
						<button 
							onClick={handleDownloadAll} 
							className="flex items-center gap-2 text-[10px] md:text-xs font-mono font-bold tracking-widest uppercase text-emerald-500 hover:text-black border border-emerald-500/30 bg-emerald-500/10 hover:bg-emerald-500 px-4 py-2 rounded-lg transition-colors"
						>
							<Archive size={14} /> Download All (.zip)
						</button>
					</div>
				)}

				{/* ROW 3: FILE LIST AREA */}
				<div className="w-full space-y-2 pb-24 md:pb-8">
					{files.length === 0 ? (
						<div className="w-full min-h-[40vh] md:min-h-75 flex flex-col items-center justify-center text-neutral-600 border-2 border-dashed border-white/5 rounded-3xl mt-4">
							<FileIcon size={48} className="mb-4 opacity-20" />
							<p className="font-mono text-sm uppercase tracking-widest text-center px-4">Awaiting Upload Sequence</p>
						</div>
					) : filteredFiles.length === 0 ? (
						<div className="text-center text-neutral-500 font-mono text-xs uppercase mt-10 tracking-widest">
							No files match query
						</div>
					) : (
						filteredFiles.map((file, idx) => (
							<div key={idx} className="group flex items-center justify-between p-3 md:p-4 bg-white/5 hover:bg-white/10 border border-white/5 hover:border-white/20 rounded-xl transition-all">
								<div className="flex items-center gap-4 overflow-hidden min-w-0 flex-1">
									<div className="p-2 bg-black/50 rounded-lg text-emerald-500 shrink-0">
										<FileIcon size={20} />
									</div>
									<p className="truncate text-sm font-mono text-neutral-200" title={file}>{file}</p>
								</div>
								<div className="flex gap-1 md:gap-2 opacity-100 md:opacity-0 group-hover:opacity-100 transition-opacity shrink-0 ml-2">
									<button onClick={() => handleDownload(file)} className="p-2 hover:bg-emerald-500/20 text-emerald-500 rounded-lg transition-colors">
										<Download size={16} />
									</button>
									<button onClick={() => setFileToDelete(file)} className="p-2 hover:bg-red-500/20 text-red-400 rounded-lg transition-colors">
										<Trash2 size={16} />
									</button>
								</div>
							</div>
						))
					)}
				</div>
			</div>
			{/* MOBILE FAB: Sticky Bottom Right (Hidden on Desktop) */}
			<button 
				onClick={() => fileInputRef.current?.click()}
				disabled={isUploading}
				className="md:hidden fixed bottom-6 right-6 w-14 h-14 bg-emerald-500 text-black rounded-2xl flex items-center justify-center shadow-[0_0_20px_rgba(16,185,129,0.4)] z-40 active:scale-95 transition-transform disabled:opacity-50"
			>
				{isUploading ? <Loader2 size={24} className="animate-spin" /> : <Plus size={24} />}
			</button>

			{/* DELETE CONFIRMATION MODAL */}
			<AnimatePresence>
				{fileToDelete && (
					<motion.div 
						initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
						className="fixed inset-0 z-100 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4"
					>
						<motion.div 
							initial={{ scale: 0.95, y: 10 }} animate={{ scale: 1, y: 0 }} exit={{ scale: 0.95, y: 10 }}
							className="bg-neutral-900 border border-white/10 p-8 rounded-2xl max-w-sm w-full text-center space-y-6 shadow-2xl"
						>
							<AlertTriangle className="text-red-500 mx-auto" size={48} />
							<div>
								<h2 className="text-white font-black uppercase tracking-widest text-lg">Purge File?</h2>
								<p className="text-neutral-400 font-mono text-xs mt-2 truncate px-4">{fileToDelete}</p>
								<p className="text-neutral-500 font-mono text-[10px] mt-2 uppercase tracking-widest">This action cannot be undone.</p>
							</div>
							<div className="flex gap-4">
								<button 
									onClick={() => setFileToDelete(null)}
									className="flex-1 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono text-xs hover:bg-white/10 transition-colors uppercase tracking-widest"
								>
									Cancel
								</button>
								<button 
									onClick={handleDelete}
									className="flex-1 py-3 bg-red-500/10 border border-red-500/30 rounded-lg text-red-500 font-bold font-mono text-xs hover:bg-red-500 hover:text-black transition-colors uppercase tracking-widest"
								>
									Confirm
								</button>
							</div>
						</motion.div>
					</motion.div>
				)}
			</AnimatePresence>
		</div>
	);
};