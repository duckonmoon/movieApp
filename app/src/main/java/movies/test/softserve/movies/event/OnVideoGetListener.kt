package movies.test.softserve.movies.event

import movies.test.softserve.movies.entity.Video

/**
 * Created by User on 06.12.2017.
 */
interface OnVideoGetListener {
    fun onVideoGet(videos: List<Video>)
}