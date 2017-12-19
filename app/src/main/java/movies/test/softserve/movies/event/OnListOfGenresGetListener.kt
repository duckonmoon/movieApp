package movies.test.softserve.movies.event

import movies.test.softserve.movies.entity.Genre

@FunctionalInterface
interface OnListOfGenresGetListener {
    fun onListOfGenresGet(genres: List<Genre>?)
}