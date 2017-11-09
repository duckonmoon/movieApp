package movies.test.softserve.movies.event

import movies.test.softserve.movies.entity.FullTVShow

/**
 * Created by rkrit on 08.11.17.
 */
interface OnFullTVShowGetListener {
    fun onFullTVShowGet(tvShow: FullTVShow)
}