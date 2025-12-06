import { useNowShowingMovies, useComingSoonMovies } from '@/hooks';

export function MoviesPage() {
  const { data: nowShowing, isLoading: loadingNow } = useNowShowingMovies();
  const { data: comingSoon, isLoading: loadingComing } = useComingSoonMovies();

  if (loadingNow || loadingComing) {
    return <div className="container mx-auto px-4 py-8">Loading movies...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <section className="mb-12">
        <h2 className="text-3xl font-bold mb-6">Now Showing</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {nowShowing?.map((movie) => (
            <div key={movie.id} className="border rounded-lg p-4">
              <h3 className="font-semibold">{movie.title}</h3>
              <p className="text-sm text-gray-600">{movie.duration} mins</p>
            </div>
          ))}
        </div>
      </section>

      <section>
        <h2 className="text-3xl font-bold mb-6">Coming Soon</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {comingSoon?.map((movie) => (
            <div key={movie.id} className="border rounded-lg p-4">
              <h3 className="font-semibold">{movie.title}</h3>
              <p className="text-sm text-gray-600">{movie.releaseDate}</p>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
