package movies.test.softserve.movies.event

import movies.test.softserve.movies.entity.Genre

/**
 * Created by rkrit on 07.11.17.
 */
interface OnListOfGenresGetListener {
    fun onListOfGenresGet(genres: List<Genre>?)
}