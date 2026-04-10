import NavBar from './home/NavBar';
import CreateSession from './home/CreateSession';
import JoinSession from './home/JoinSession';
import CreatorCard from './home/CreatorCard';

export default function HomePage() {
	return (
		<div className='h-screen bg-emerald-800 '>
			<NavBar />
			<div className='flex flex-col items-center gap-2'>
				<h1 className='text-neutral-200 drop-shadow-md'>
					A
					<span className='text-amber-400/80 font-serif text-shadow-2xs '>
						{' '}
						secure{' '}
					</span>
					way to share files
				</h1>
				<p>
					cross all your smart devices
				</p>
				<CreateSession />
				<JoinSession />
				<CreatorCard />
			</div>
		</div>
	);
}
