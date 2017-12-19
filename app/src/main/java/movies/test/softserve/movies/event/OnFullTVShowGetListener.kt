package movies.test.softserve.movies.event

import movies.test.softserve.movies.entity.FullTVShow

@FunctionalInterface
interface OnFullTVShowGetListener {
    fun onFullTVShowGet(tvShow: FullTVShow)
}