package movies.test.softserve.movies.event

import movies.test.softserve.movies.entity.Movie

/**
 * Created by rkrit on 06.11.17.
 */
interface OnListOfMoviesGetListener {
    fun onListOfMoviesGetListener(movies: List<Movie>)
}