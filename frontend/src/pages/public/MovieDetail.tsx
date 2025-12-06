import { useParams } from 'react-router-dom';
import { useMovie } from '@/hooks';

export function MovieDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: movie, isLoading } = useMovie(Number(id));

  if (isLoading) {
    return <div className="container mx-auto px-4 py-8">Loading...</div>;
  }

  if (!movie) {
    return <div className="container mx-auto px-4 py-8">Movie not found</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-4xl font-bold mb-4">{movie.title}</h1>
      <p className="text-xl text-gray-600 mb-4">{movie.originalTitle}</p>
      <div className="grid grid-cols-2 gap-4 mb-6">
        <div>
          <span className="font-semibold">Duration:</span> {movie.duration} mins
        </div>
        <div>
          <span className="font-semibold">Age Rating:</span> {movie.rating}
        </div>
        <div>
          <span className="font-semibold">Director:</span> {movie.director}
        </div>
        <div>
          <span className="font-semibold">Language:</span> {movie.language}
        </div>
      </div>
      <p className="text-gray-700">{movie.description}</p>
    </div>
  );
}
