package movies.test.softserve.movies.event

import movies.test.softserve.movies.entity.TVShow

/**
 * Created by rkrit on 07.11.17.
 */
interface OnListOfTVShowsGetListener {
    fun onListOfTVShowsGet(tvShows: List<TVShow>)
}