package movies.test.softserve.movies.service

import movies.test.softserve.movies.controller.MainController
import movies.test.softserve.movies.dao.GenreDao
import movies.test.softserve.movies.dao.MovieDao
import movies.test.softserve.movies.db.entity.Movie
import movies.test.softserve.movies.entity.TVEntity
import movies.test.softserve.movies.util.AchievementService
import movies.test.softserve.movies.util.Mapper
import movies.test.softserve.movies.util.RatingService

/**need to be asynchronous*/
class DbMovieServiceRoom private constructor() {
    companion object {
        private var INSTANCE: DbMovieServiceRoom? = null

        fun getInstance(): DbMovieServiceRoom {
            if (INSTANCE == null) {
                INSTANCE = DbMovieServiceRoom()
            }
            return INSTANCE!!
        }
    }


    private var ratingService : RatingService

    private var database: AppRoomDatabase = MainController.getInstance().database
    private var movieDao: MovieDao
    private var genreDao: GenreDao

    init {
        movieDao = database.movieDao()
        genreDao = database.genreDao()
        ratingService = RatingService.getInstance()
    }

    /**need to be asynchronous*/
    fun insertFavouriteTVEntity(tvEntity: TVEntity) {
        val movie: Movie = Mapper.mapFromTVEntityToDbMovie(tvEntity, Mapper.FAVOURITE)
        movieDao.insertMovie(movie)
        genreDao.insertGenres(Mapper.mapFromIntegersToDbGenres(tvEntity.genreIds, tvEntity))
        AchievementService.getInstance().checkWhatAchievementsIsDone()
        ratingService.change(tvEntity.voteAverage.toFloat(),RatingService.ADD)
    }

    /**need to be asynchronous*/
    fun insertTVEntity(tvEntity: TVEntity) {
        val movie: Movie = Mapper.mapFromTVEntityToDbMovie(tvEntity, Mapper.NOT_FAVOURITE)
        movieDao.insertMovie(movie)
        genreDao.insertGenres(Mapper.mapFromIntegersToDbGenres(tvEntity.genreIds, tvEntity))
        AchievementService.getInstance().checkWhatAchievementsIsDone()
        ratingService.change(tvEntity.voteAverage.toFloat(),RatingService.ADD)
    }

    /**need to be asynchronous*/
    fun deleteFromDb(tvEntity: TVEntity) {
        movieDao.deleteMovie(tvEntity.id)
        ratingService.change(tvEntity.voteAverage.toFloat(),RatingService.SUB)
    }

    /**need to be asynchronous*/
    fun getAll(type: TVEntity.TYPE): List<TVEntity> {
        return Mapper.mapFromMovieWithGenreToTVEntity(movieDao.loadAllMovies(type.toString()))
    }

    /**need to be asynchronous*/
    fun getAllFavourite(type: TVEntity.TYPE): List<TVEntity> {
        return Mapper.mapFromMovieWithGenreToTVEntity(movieDao.loadAllFavouriteMovies(type.toString()))
    }

    /**need to be asynchronous*/
    fun checkIfExists(tvEntity: TVEntity): Boolean {
        return movieDao.checkIfMovieExists(tvEntity.id) > 0
    }

    /**need to be asynchronous*/
    fun checkIfIsFavourite(tvEntity: TVEntity): Boolean {
        return movieDao.checkIfIsFavourite(tvEntity.id) > 0
    }

    /**need to be asynchronous*/
    fun setFavourite(tvEntity: TVEntity) {
        movieDao.updateFavorite(tvEntity.id, 1)
    }

    /**need to be asynchronous*/
    fun cancelFavourite(tvEntity: TVEntity) {
        movieDao.updateFavorite(tvEntity.id, 0)
    }


    /**need to be asynchronous*/
    fun getMovieCount(type: TVEntity.TYPE): Int {
        return movieDao.moviesSize(type.toString())
    }


    /**need to be asynchronous*/
    fun getMoviesSizeWithGenre(genre: Int, type: TVEntity.TYPE): Int {
        return movieDao.getMoviesSizeWithGenre(type.toString(), genre)
    }
}