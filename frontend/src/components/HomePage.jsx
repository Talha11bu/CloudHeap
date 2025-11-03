import NavBar from './NavBar';
import CreateSession from './CreateSession';
import JoinSession from './JoinSession';
import CreatorCard from './CreatorCard';

export default function HomePage() {
	return (
		<div className='h-screen bg-emerald-500'>
			<NavBar />
			<div className='flex flex-col items-center'>
				<h1 className='text-neutral-900'>
					An<span className='text-amber-300 font-serif'> Easier </span>way to
					share files
				</h1>
				<p></p>
				<CreateSession />
				<JoinSession />
				<CreatorCard />
			</div>
		</div>
	);
}
