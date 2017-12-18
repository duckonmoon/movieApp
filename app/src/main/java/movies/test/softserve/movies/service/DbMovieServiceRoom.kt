package movies.test.softserve.movies.service

import movies.test.softserve.movies.controller.MainController
import movies.test.softserve.movies.dao.GenreDao
import movies.test.softserve.movies.dao.MovieDao
import movies.test.softserve.movies.db.entity.Movie
import movies.test.softserve.movies.db.entity.MovieWithTheGenre
import movies.test.softserve.movies.entity.TVEntity
import movies.test.softserve.movies.util.Mapper

/**
 * Created by root on 15.12.17.
 */
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
            genreDao.insertGenres(Mapper.mapFromIntegersToDbGenres(tvEntity.genreIds, tvEntity))
        }).start()
    }

    fun insertTVEntity(tvEntity: TVEntity) {
        Thread(Runnable {
            val movie: Movie = Mapper.mapFromTVEntityToDbMovie(tvEntity, Mapper.NOT_FAVOURITE)
            movieDao.insertMovie(movie)
            genreDao.insertGenres(Mapper.mapFromIntegersToDbGenres(tvEntity.genreIds, tvEntity))
        }).start()

    }

    fun deleteFromDb(tvEntity: TVEntity) {
        Thread(Runnable {
            movieDao.deleteMovie(tvEntity.id)
        }).start()
    }

    /**need to be asynchronous*/
    fun getAll(type: TVEntity.TYPE): List<MovieWithTheGenre> {
        return movieDao.loadAllMovies(type.toString())
    }

    /**need to be asynchronous*/
    fun getAllFavourite(type: TVEntity.TYPE): List<MovieWithTheGenre> {
        return movieDao.loadAllFavouriteMovies(type.toString())
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