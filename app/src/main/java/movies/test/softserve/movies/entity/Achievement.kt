package movies.test.softserve.movies.entity

import movies.test.softserve.movies.R
import movies.test.softserve.movies.constans.Constants.*

/**
 * Created by rkrit on 15.11.17.
 */

class Achievement private constructor(var resourceId: Int, var title: String?, var description: String?,var clazz: TVType,var genre: Int?,var number: Int) {
    companion object {
        fun getAchievements(): ArrayList<Achievement> {
            val achievements: ArrayList<Achievement> = ArrayList()
            achievements.add(Achievement(R.mipmap.donat, "Beginner", "Watch 10 movies", TVType.MOVIE, null, 10))
            achievements.add(Achievement(R.mipmap.fuck_yeah, "Movie Lover", "Watch 100 movies", TVType.MOVIE, null, 100))
            achievements.add(Achievement(R.mipmap.bat_girl, "Movie God", "Watch 1000 movies", TVType.MOVIE, null, 1000))
            achievements.add(Achievement(R.mipmap.unicorn, "Unicorn", "Watch 5 TV Shows", TVType.TV_SHOW, null, 5))
            achievements.add(Achievement(R.mipmap.ice_cream, "TV Show Addicted", "Watch 20 TV Shows", TVType.TV_SHOW, null, 20))
            achievements.add(Achievement(R.mipmap.pickachu, "Is it even possible?", "Watch 100 TV Shows", TVType.TV_SHOW, null, 100))
            achievements.add(Achievement(R.mipmap.horror_movie, "Horror Lover","Watch 5 horror movies",TVType.MOVIE,27,5))
            return achievements
        }
    }

}
