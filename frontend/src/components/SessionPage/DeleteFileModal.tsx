import { AlertTriangle } from 'lucide-react';
import { motion } from 'framer-motion';

interface DeleteFileModalProps {
	fileName: string;
	onConfirm: () => void;
	onCancel: () => void;
}

/** Confirmation modal for permanently deleting a file from R2. */
export const DeleteFileModal = ({
	fileName,
	onConfirm,
	onCancel,
}: DeleteFileModalProps) => (
	<motion.div
		initial={{ opacity: 0 }}
		animate={{ opacity: 1 }}
		exit={{ opacity: 0 }}
		className="fixed inset-0 z-100 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4"
	>
		<motion.div
			initial={{ scale: 0.95, y: 10 }}
			animate={{ scale: 1, y: 0 }}
			exit={{ scale: 0.95, y: 10 }}
			className="bg-neutral-900 border border-white/10 p-8 rounded-2xl max-w-sm w-full text-center space-y-6 shadow-2xl"
		>
			<AlertTriangle className="text-red-500 mx-auto" size={48} />
			<div>
				<h2 className="text-white font-black uppercase tracking-widest text-lg">
					Purge File?
				</h2>
				<p className="text-neutral-400 font-mono text-xs mt-2 truncate px-4">
					{fileName}
				</p>
				<p className="text-neutral-500 font-mono text-[10px] mt-2 uppercase tracking-widest">
					This action cannot be undone.
				</p>
			</div>
			<div className="flex gap-4">
				<button
					onClick={onCancel}
					className="flex-1 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono text-xs hover:bg-white/10 transition-colors uppercase tracking-widest"
				>
					Cancel
				</button>
				<button
					onClick={onConfirm}
					className="flex-1 py-3 bg-red-500/10 border border-red-500/30 rounded-lg text-red-500 font-bold font-mono text-xs hover:bg-red-500 hover:text-black transition-colors uppercase tracking-widest"
				>
					Confirm
				</button>
			</div>
		</motion.div>
	</motion.div>
);
