package uz.ttpu.movieshelf.data.repository

import uz.ttpu.movieshelf.data.local.MovieLocalDataSource
import uz.ttpu.movieshelf.data.mapper.toDomain
import uz.ttpu.movieshelf.data.remote.MovieRemoteDataSource
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class MovieRepositoryImpl(
    private val remote: MovieRemoteDataSource,
    private val local: MovieLocalDataSource,
) : MovieRepository {

    override suspend fun getMovies(): MoviesResult {
        val favoriteIds = local.getFavoriteIds()
        return try {
            val remoteDtos = remote.fetchMovies()
            local.saveMovies(remoteDtos)
            val domainMovies = remoteDtos.map { dto ->
                dto.toDomain(isFavorite = dto.id in favoriteIds)
            }
            MoviesResult(movies = domainMovies, isFromCache = false)
        } catch (e: IOException) {
            val cachedDtos = local.getCachedMovies()
            if (cachedDtos != null) {
                val domainMovies = cachedDtos.map { dto ->
                    dto.toDomain(isFavorite = dto.id in favoriteIds)
                }
                MoviesResult(movies = domainMovies, isFromCache = true)
            } else {
                throw e
            }
        } catch (e: CancellationException) {
            throw e
        }
    }

    override suspend fun toggleFavorite(movieId: Int): Boolean {
        val currentFavorites = local.getFavoriteIds()
        val isFav = movieId in currentFavorites
        val newFav = !isFav
        local.setFavorite(movieId, newFav)
        return newFav
    }
}
