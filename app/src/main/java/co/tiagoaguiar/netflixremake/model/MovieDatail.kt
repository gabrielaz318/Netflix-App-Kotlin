package co.tiagoaguiar.netflixremake.model

data class MovieDatail(
    val movie: Movie,
    val similars: List<Movie>
)
