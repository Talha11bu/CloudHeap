import { File as FileIcon, Download, Trash2 } from 'lucide-react';

interface FileListItemProps {
	fileName: string;
	onDownload: (fileName: string) => void;
	onDelete: (fileName: string) => void;
}

/** A single file row with hover-reveal download/delete actions. */
export const FileListItem = ({
	fileName,
	onDownload,
	onDelete,
}: FileListItemProps) => (
	<div className="group flex items-center justify-between p-3 md:p-4 bg-white/5 hover:bg-white/10 border border-white/5 hover:border-white/20 rounded-xl transition-all">
		<div className="flex items-center gap-4 overflow-hidden min-w-0 flex-1">
			<div className="p-2 bg-black/50 rounded-lg text-emerald-500 shrink-0">
				<FileIcon size={20} />
			</div>
			<p className="truncate text-sm font-mono text-neutral-200" title={fileName}>
				{fileName}
			</p>
		</div>
		<div className="flex gap-1 md:gap-2 opacity-100 md:opacity-0 group-hover:opacity-100 transition-opacity shrink-0 ml-2">
			<button
				onClick={() => onDownload(fileName)}
				className="p-2 hover:bg-emerald-500/20 text-emerald-500 rounded-lg transition-colors"
			>
				<Download size={16} />
			</button>
			<button
				onClick={() => onDelete(fileName)}
				className="p-2 hover:bg-red-500/20 text-red-400 rounded-lg transition-colors"
			>
				<Trash2 size={16} />
			</button>
		</div>
	</div>
);
