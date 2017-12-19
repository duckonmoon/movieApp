package movies.test.softserve.movies.event

import movies.test.softserve.movies.entity.TVEntity

@FunctionalInterface
interface OnListOfMoviesGetListener {
    fun onListOfMoviesGetListener(movies: List<TVEntity>)
}