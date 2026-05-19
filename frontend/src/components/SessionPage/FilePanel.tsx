import { useState, useRef } from 'react';
import {
	Upload,
	Search,
	File as FileIcon,
	Loader2,
	Plus,
	Archive,
} from 'lucide-react';
import { AnimatePresence } from 'framer-motion';
import {
	getUploadUrl,
	uploadToR2,
	confirmUpload,
	getDownloadUrl,
	downloadZip,
	deleteSessionFile,
} from '../../api/fileApi';
import { FileListItem } from './FileListItem';
import { DeleteFileModal } from './DeleteFileModal';

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

	const filteredFiles = files.filter((f) =>
		f.toLowerCase().includes(searchQuery.toLowerCase())
	);

	const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
		const selectedFile = e.target.files?.[0];
		if (!selectedFile) return;

		setIsUploading(true);

		try {
			const token = localStorage.getItem('silk_road_jwt');
			if (!token) throw new Error('No authentication token found');

			const contentType = selectedFile.type || 'application/octet-stream';
			const { url, fileKey } = await getUploadUrl(
				sessionId,
				selectedFile.name,
				contentType,
				token
			);

			await uploadToR2(url, selectedFile);
			await confirmUpload(
				sessionId,
				selectedFile.name,
				fileKey,
				selectedFile.size,
				token
			);
		} catch (error) {
			console.error('Upload failed', error);
		} finally {
			setIsUploading(false);
			if (fileInputRef.current) fileInputRef.current.value = '';
		}
	};

	const handleDownload = async (fileName: string) => {
		try {
			const token = localStorage.getItem('silk_road_jwt');
			if (!token) return;

			const url = await getDownloadUrl(sessionId, fileName, token);
			if (url) {
				const link = document.createElement('a');
				link.href = url;
				document.body.appendChild(link);
				link.click();
				document.body.removeChild(link);
			}
		} catch (error) {
			console.error('Download failed', error);
		}
	};

	const handleDownloadAll = async () => {
		try {
			const token = localStorage.getItem('silk_road_jwt');
			if (!token) return;

			const blob = await downloadZip(sessionId, password, token);
			const blobUrl = window.URL.createObjectURL(blob);
			const link = document.createElement('a');
			link.href = blobUrl;
			link.download = `SilkRoad_${sessionId}_Archive.zip`;
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
			window.URL.revokeObjectURL(blobUrl);
		} catch (error) {
			console.error('Zip download failed', error);
		}
	};

	const handleDelete = async () => {
		if (!fileToDelete) return;
		try {
			await deleteSessionFile(sessionId, fileToDelete);
		} catch (error) {
			console.error('Delete failed', error);
		} finally {
			setFileToDelete(null);
		}
	};

	return (
		<div className="flex flex-col w-full min-h-full p-4 md:p-8 bg-[radial-gradient(circle_at_center,rgba(16,185,129,0.03)_0%,transparent_60%)]">
			{/* HIDDEN FILE INPUT */}
			<input
				type="file"
				className="hidden"
				ref={fileInputRef}
				onChange={handleUpload}
			/>

			<div className="w-full max-w-5xl mx-auto flex flex-col">
				{/* Search & Upload Row */}
				<div className="flex flex-col md:flex-row items-stretch md:items-center gap-4 mb-4 w-full">
					<div className="relative flex-1 w-full">
						<Search
							size={16}
							className="absolute left-3 top-1/2 -translate-y-1/2 text-neutral-500"
						/>
						<input
							type="text"
							placeholder="SEARCH FILES..."
							value={searchQuery}
							onChange={(e) => setSearchQuery(e.target.value)}
							className="w-full bg-black/50 border border-white/10 rounded-xl pl-10 pr-4 py-3 text-xs text-white font-mono uppercase tracking-widest outline-none focus:border-emerald-500/50 transition-colors placeholder:text-neutral-600"
						/>
					</div>

					<button
						onClick={() => fileInputRef.current?.click()}
						disabled={isUploading}
						className="hidden md:flex shrink-0 items-center justify-center gap-2 bg-emerald-500 text-black px-6 py-3 rounded-xl font-bold text-xs uppercase tracking-widest hover:bg-emerald-400 transition-all disabled:opacity-50 shadow-[0_0_15px_rgba(16,185,129,0.2)]"
					>
						{isUploading ? (
							<Loader2 size={16} className="animate-spin" />
						) : (
							<Upload size={16} />
						)}
						Upload File
					</button>
				</div>

				{/* Download All ZIP */}
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

				{/* File List */}
				<div className="w-full space-y-2 pb-24 md:pb-8">
					{files.length === 0 ? (
						<div className="w-full min-h-[40vh] md:min-h-75 flex flex-col items-center justify-center text-neutral-600 border-2 border-dashed border-white/5 rounded-3xl mt-4">
							<FileIcon size={48} className="mb-4 opacity-20" />
							<p className="font-mono text-sm uppercase tracking-widest text-center px-4">
								Awaiting Upload Sequence
							</p>
						</div>
					) : filteredFiles.length === 0 ? (
						<div className="text-center text-neutral-500 font-mono text-xs uppercase mt-10 tracking-widest">
							No files match query
						</div>
					) : (
						filteredFiles.map((file, idx) => (
							<FileListItem
								key={idx}
								fileName={file}
								onDownload={handleDownload}
								onDelete={setFileToDelete}
							/>
						))
					)}
				</div>
			</div>

			{/* MOBILE FAB */}
			<button
				onClick={() => fileInputRef.current?.click()}
				disabled={isUploading}
				className="md:hidden fixed bottom-6 right-6 w-14 h-14 bg-emerald-500 text-black rounded-2xl flex items-center justify-center shadow-[0_0_20px_rgba(16,185,129,0.4)] z-40 active:scale-95 transition-transform disabled:opacity-50"
			>
				{isUploading ? (
					<Loader2 size={24} className="animate-spin" />
				) : (
					<Plus size={24} />
				)}
			</button>

			{/* DELETE CONFIRMATION MODAL */}
			<AnimatePresence>
				{fileToDelete && (
					<DeleteFileModal
						fileName={fileToDelete}
						onConfirm={handleDelete}
						onCancel={() => setFileToDelete(null)}
					/>
				)}
			</AnimatePresence>
		</div>
	);
};