package uz.ttpu.movieshelf

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase

class FakeMovieRepository(
    private val result: MoviesResult,
) : MovieRepository {
    override suspend fun getMovies() = result
    override suspend fun toggleFavorite(movieId: Int) = true
}

class GetMoviesUseCaseTest {

    @Test
    fun movies_are_sorted_by_rating_desc_then_title() = runBlocking<Unit> {
        val movieB = Movie(1, "B", 2020, 8.0, false)
        val movieA = Movie(2, "A", 2020, 8.0, false)
        val movieC = Movie(3, "C", 2020, 9.1, false)

        val repository = FakeMovieRepository(
            MoviesResult(listOf(movieB, movieA, movieC), isFromCache = false)
        )
        val useCase = GetMoviesUseCase(repository)

        val result = useCase()

        val titles = result.movies.map { it.title }
        assertEquals(listOf("C", "A", "B"), titles)
    }

    @Test
    fun is_from_cache_flag_is_passed_through() = runBlocking<Unit> {
        val repository = FakeMovieRepository(
            MoviesResult(emptyList(), isFromCache = true)
        )
        val useCase = GetMoviesUseCase(repository)

        val result = useCase()

        assertTrue(result.isFromCache)
    }

    @Test
    fun get_favorite_movies_use_case_returns_only_favorites() = runBlocking<Unit> {
        val movie1 = Movie(1, "Movie 1", 2020, 8.0, true)
        val movie2 = Movie(2, "Movie 2", 2021, 7.5, false)
        val repository = FakeMovieRepository(
            MoviesResult(listOf(movie1, movie2), isFromCache = false)
        )
        val getMoviesUseCase = GetMoviesUseCase(repository)
        val getFavoriteMoviesUseCase = GetFavoriteMoviesUseCase(getMoviesUseCase)

        val result = getFavoriteMoviesUseCase()

        assertEquals(1, result.movies.size)
        assertEquals("Movie 1", result.movies.first().title)
        assertTrue(result.movies.first().isFavorite)
    }
}
