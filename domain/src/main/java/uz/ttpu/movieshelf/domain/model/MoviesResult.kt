package uz.ttpu.movieshelf.domain.model

data class MoviesResult(
    val movies: List<Movie>,
    val isFromCache: Boolean,   // true = the network failed and this is the saved copy
)
