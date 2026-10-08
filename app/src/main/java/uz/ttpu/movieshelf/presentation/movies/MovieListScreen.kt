package uz.ttpu.movieshelf.presentation.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.movieshelf.domain.model.Movie

@Composable
fun MovieListScreen(
    state: MovieListUiState,
    onFavoriteClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onFavoritesOnlyChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (state) {
            is MovieListUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is MovieListUiState.Error -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh) {
                        Text("Retry")
                    }
                }
            }
            is MovieListUiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onRefresh) {
                            Text("Refresh")
                        }
                        FilterChip(
                            selected = state.favoritesOnly,
                            onClick = { onFavoritesOnlyChange(!state.favoritesOnly) },
                            label = { Text("Favorites only") }
                        )
                    }

                    if (state.isFromCache) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Offline — showing saved data",
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.movies,
                            key = { it.id }
                        ) { movie ->
                            MovieItem(
                                movie = movie,
                                onFavoriteClick = { onFavoriteClick(movie.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${movie.year} · Rating ${movie.rating}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (movie.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (movie.isFavorite) "Favorite" else "Not favorite",
                    tint = if (movie.isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieListLoadingPreview() {
    MaterialTheme {
        MovieListScreen(
            state = MovieListUiState.Loading,
            onFavoriteClick = {},
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MovieListSuccessPreview() {
    MaterialTheme {
        MovieListScreen(
            state = MovieListUiState.Success(
                movies = listOf(
                    Movie(1, "The Silk Road Express", 2019, 8.4, false),
                    Movie(2, "Midnight in Samarkand", 2021, 7.9, true)
                ),
                isFromCache = false
            ),
            onFavoriteClick = {},
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MovieListSuccessCachedPreview() {
    MaterialTheme {
        MovieListScreen(
            state = MovieListUiState.Success(
                movies = listOf(
                    Movie(1, "The Silk Road Express", 2019, 8.4, false),
                    Movie(2, "Midnight in Samarkand", 2021, 7.9, true)
                ),
                isFromCache = true
            ),
            onFavoriteClick = {},
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MovieListErrorPreview() {
    MaterialTheme {
        MovieListScreen(
            state = MovieListUiState.Error("Can't load movies. Check your connection and try again."),
            onFavoriteClick = {},
            onRefresh = {}
        )
    }
}
