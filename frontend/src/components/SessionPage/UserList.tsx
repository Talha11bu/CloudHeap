interface UserListProps {
	users: string[];
	clientUsername: string;
}

/** Renders a list of connected agents with a pulsing green indicator. */
export const UserList = ({ users, clientUsername }: UserListProps) => (
	<ul className="space-y-3 overflow-y-auto">
		{users.map((user, idx) => (
			<li
				key={idx}
				className="flex items-center gap-3 text-sm text-neutral-300 font-mono"
			>
				<span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
				{user}{' '}
				{user === clientUsername && (
					<span className="text-emerald-500/50 text-[10px]">(You)</span>
				)}
			</li>
		))}
	</ul>
);
