package movies.test.softserve.movies.entity

import movies.test.softserve.movies.R
import movies.test.softserve.movies.constans.Constants.*
import java.util.*

/**
 * Created by rkrit on 15.11.17.
 */

class Achievement private constructor(var resourceId: Int, var title: String?, var description: String?, var clazz: TVType, var genre: Int?, var number: Int) {
    companion object {
        fun getAchievements(): ArrayList<Achievement> {
            val achievements: ArrayList<Achievement> = ArrayList()
            if (Locale.getDefault().language == "uk") {
                achievements.add(Achievement(R.mipmap.donat, "Початківець", "Переглянути 10 фільмів", TVType.MOVIE, null, 10))
                achievements.add(Achievement(R.mipmap.fuck_yeah, "Любитель мультиків", "Переглянути 100 фільмів", TVType.MOVIE, null, 100))
                achievements.add(Achievement(R.mipmap.bat_girl, "Бог мультиків", "Переглянути 1000 фільмів", TVType.MOVIE, null, 1000))
                achievements.add(Achievement(R.mipmap.unicorn, "Єдиноріг", "Переглянути 5 серіалів", TVType.TV_SHOW, null, 5))
                achievements.add(Achievement(R.mipmap.ice_cream, "Залежний", "Переглянути 20 серіалів", TVType.TV_SHOW, null, 20))
                achievements.add(Achievement(R.mipmap.pickachu, "Це взагалі можливо?", "Переглянути 100 серіалів", TVType.TV_SHOW, null, 100))
                achievements.add(Achievement(R.mipmap.horror_movie, "Наляканий хлопчисько", "Переглянути 5 фільмів жахів", TVType.MOVIE, 27, 5))
                achievements.add(Achievement(R.mipmap.ic_horror, "Любитель жахів", "Переглянути 25 фільмів жахів", TVType.MOVIE, 27, 25))
                achievements.add(Achievement(R.mipmap.ic_action, "Джеймс Бонд", "Переглянути 25 бойовиків", TVType.MOVIE, 28, 25))
                achievements.add(Achievement(R.mipmap.ic_animation, "Школяр", "Переглянути 5 мультиків", TVType.MOVIE, 16, 5))
                achievements.add(Achievement(R.mipmap.ic_cartoon, "Дитина", "Переглянути 25 мультиків", TVType.MOVIE, 16, 25))
                achievements.add(Achievement(R.mipmap.ic_crime, "Студент", "Переглянути 5 кримінальних фільмів", TVType.MOVIE, 80, 5))
                achievements.add(Achievement(R.mipmap.ic_crime1, "Детектив", "Переглянути 25 кримінальних фільмів", TVType.MOVIE, 80, 25))
                achievements.add(Achievement(R.mipmap.ic_crime2, "Шерлок", "Переглянути 50 кримінальних фільмів", TVType.MOVIE, 80, 50))
            } else {
                achievements.add(Achievement(R.mipmap.donat, "Beginner", "Watch 10 movies", TVType.MOVIE, null, 10))
                achievements.add(Achievement(R.mipmap.fuck_yeah, "Movie Lover", "Watch 100 movies", TVType.MOVIE, null, 100))
                achievements.add(Achievement(R.mipmap.bat_girl, "Movie God", "Watch 1000 movies", TVType.MOVIE, null, 1000))
                achievements.add(Achievement(R.mipmap.unicorn, "Unicorn", "Watch 5 TV Shows", TVType.TV_SHOW, null, 5))
                achievements.add(Achievement(R.mipmap.ice_cream, "TV Show Addicted", "Watch 20 TV Shows", TVType.TV_SHOW, null, 20))
                achievements.add(Achievement(R.mipmap.pickachu, "Is it even possible?", "Watch 100 TV Shows", TVType.TV_SHOW, null, 100))
                achievements.add(Achievement(R.mipmap.horror_movie, "Scary boy", "Watch 5 horror movies", TVType.MOVIE, 27, 5))
                achievements.add(Achievement(R.mipmap.ic_horror, "Horror Lover", "Watch 25 horror movies", TVType.MOVIE, 27, 25))
                achievements.add(Achievement(R.mipmap.ic_action, "James Bond", "Watch 25 action movies", TVType.MOVIE, 28, 25))
                achievements.add(Achievement(R.mipmap.ic_animation, "Scholar", "Watch 5 cartoons", TVType.MOVIE, 16, 5))
                achievements.add(Achievement(R.mipmap.ic_cartoon, "Kid", "Watch 25 cartoons", TVType.MOVIE, 16, 25))
                achievements.add(Achievement(R.mipmap.ic_crime, "Student", "Watch 5 crime movies", TVType.MOVIE, 80, 5))
                achievements.add(Achievement(R.mipmap.ic_crime1, "Detective", "Watch 25 crime movies", TVType.MOVIE, 80, 25))
                achievements.add(Achievement(R.mipmap.ic_crime2, "Sherlock", "Watch 50 crime movies", TVType.MOVIE, 80, 50))
            }
            return achievements
        }
    }

}
