package uz.ttpu.movieshelf.data.local

import android.content.Context
import uz.ttpu.movieshelf.data.remote.MovieDto

class SharedPrefsMovieLocalDataSource(context: Context) : MovieLocalDataSource {
    private val prefs = context.getSharedPreferences("movieshelf", Context.MODE_PRIVATE)
    private var cached: List<MovieDto>? = null

    override suspend fun getCachedMovies(): List<MovieDto>? {
        return cached
    }

    override suspend fun saveMovies(movies: List<MovieDto>) {
        cached = movies
    }

    override suspend fun getFavoriteIds(): Set<Int> {
        val stringSet = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
        val intSet = mutableSetOf<Int>()
        for (str in stringSet) {
            val id = str.toIntOrNull()
            if (id != null) {
                intSet.add(id)
            }
        }
        return intSet
    }

    override suspend fun setFavorite(id: Int, favorite: Boolean) {
        val stringSet = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
        val mutableFavorites = stringSet.toMutableSet()
        if (favorite) {
            mutableFavorites.add(id.toString())
        } else {
            mutableFavorites.remove(id.toString())
        }
        prefs.edit().putStringSet("favorites", mutableFavorites).apply()
    }
}
