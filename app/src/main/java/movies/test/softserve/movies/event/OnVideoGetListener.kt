package movies.test.softserve.movies.event

import movies.test.softserve.movies.entity.Video

@FunctionalInterface
interface OnVideoGetListener {
    fun onVideoGet(videos: List<Video>)
}