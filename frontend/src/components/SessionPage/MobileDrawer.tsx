import { Users, X } from 'lucide-react';
import { motion } from 'framer-motion';
import { UserList } from './UserList';

interface MobileDrawerProps {
	isOpen: boolean;
	onClose: () => void;
	users: string[];
	clientUsername: string;
}

/** Discord-style mobile slide-in drawer showing connected agents. */
export const MobileDrawer = ({
	isOpen,
	onClose,
	users,
	clientUsername,
}: MobileDrawerProps) => {
	if (!isOpen) return null;

	return (
		<>
			{/* Backdrop */}
			<motion.div
				initial={{ opacity: 0 }}
				animate={{ opacity: 1 }}
				exit={{ opacity: 0 }}
				onClick={onClose}
				className="md:hidden fixed inset-0 bg-black/80 backdrop-blur-sm z-40"
			/>

			{/* Sliding Menu */}
			<motion.div
				initial={{ x: '100%' }}
				animate={{ x: 0 }}
				exit={{ x: '100%' }}
				transition={{ type: 'spring', damping: 25, stiffness: 200 }}
				className="md:hidden fixed inset-y-0 right-0 w-64 bg-neutral-950 border-l border-white/10 z-50 p-6 flex flex-col shadow-2xl"
			>
				<div className="flex items-center justify-between border-b border-white/10 pb-4 mb-4">
					<h2 className="text-xs font-bold uppercase tracking-widest text-emerald-500 flex items-center gap-2">
						<Users size={16} /> Agents ({users.length})
					</h2>
					<button
						onClick={onClose}
						className="p-1 hover:bg-white/10 rounded-md"
					>
						<X size={20} className="text-neutral-400" />
					</button>
				</div>

				<UserList users={users} clientUsername={clientUsername} />
			</motion.div>
		</>
	);
};
