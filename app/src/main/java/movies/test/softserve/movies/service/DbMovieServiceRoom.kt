package movies.test.softserve.movies.service

import movies.test.softserve.movies.controller.MainController
import movies.test.softserve.movies.dao.GenreDao
import movies.test.softserve.movies.dao.MovieDao
import movies.test.softserve.movies.db.entity.Movie
import movies.test.softserve.movies.entity.TVEntity
import movies.test.softserve.movies.util.AchievementService
import movies.test.softserve.movies.util.Mapper
import movies.test.softserve.movies.util.RatingService

/**
 * Created by root on 15.12.17.
 */
class DbMovieServiceRoom private constructor() {
    companion object {
        val CONTENT_TYPE_MOVIE = 1
        val CONTENT_TYPE_TVSHOW = 0
        var INSTANCE: DbMovieServiceRoom? = null

        fun getInstance(): DbMovieServiceRoom {
            if (INSTANCE == null) {
                INSTANCE = DbMovieServiceRoom()
            }
            return INSTANCE!!
        }
    }

    private var ratingService = RatingService.getInstance()
    private var achievementService = AchievementService.getInstance()


    private var database: AppRoomDatabase = MainController.getInstance().database
    private var movieDao: MovieDao
    private var genreDao: GenreDao

    init {
        movieDao = database.movieDao()
        genreDao = database.genreDao()
    }

    fun insertFavouriteTVEntity(tvEntity: TVEntity) {
        Thread(Runnable {
            val movie: Movie = Mapper.mapFromTVEntityToDbMovie(tvEntity, Mapper.FAVOURITE)
            movieDao.insertMovie(movie)
            genreDao.insertGenres(Mapper.mapFromIntegersToDbGenres(tvEntity.genreIds,tvEntity))
            ratingService.change(tvEntity.voteAverage.toFloat(), RatingService.ADD)
            achievementService.checkWhatAchievementsIsDone()
        }).start()
    }

    fun insertTVEntity(tvEntity: TVEntity){
        Thread(Runnable {
            val movie: Movie = Mapper.mapFromTVEntityToDbMovie(tvEntity, Mapper.NOT_FAVOURITE)
            movieDao.insertMovie(movie)
            genreDao.insertGenres(Mapper.mapFromIntegersToDbGenres(tvEntity.genreIds,tvEntity))
            ratingService.change(tvEntity.voteAverage.toFloat(), RatingService.ADD)
            achievementService.checkWhatAchievementsIsDone()
        }).start()

    }

    fun deleteFromDb(tvEntity: TVEntity){

    }

    fun checkIfExists(tvEntity: TVEntity) : Boolean {
        return movieDao.checkIfMovieExists(tvEntity.id) <= 0
    }

    fun checkIfIsFavourite(tvEntity: TVEntity): Boolean {
        return movieDao.checkIfIsFavourite(tvEntity.id) <= 0
    }
}