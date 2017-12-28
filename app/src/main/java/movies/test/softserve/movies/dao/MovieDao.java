package movies.test.softserve.movies.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import movies.test.softserve.movies.db.entity.Movie;
import movies.test.softserve.movies.db.entity.MovieWithTheGenre;

/**
 * Created by root on 15.12.17.
 */

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie... movies);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(List<Movie> movies);

    @Transaction
    @Query("Select * from movie where type = :type")
    List<MovieWithTheGenre> loadAllMovies(String type);

    @Transaction
    @Query("Select * from movie")
    List<MovieWithTheGenre> loadAllMovies();

    @Transaction
    @Query("Select * from movie where type = :type and favourite != 0")
    List<MovieWithTheGenre> loadAllFavouriteMovies(String type);

    @Transaction
    @Query("Select * from movie where favourite != 0")
    List<MovieWithTheGenre> loadAllFavouriteMoviess();

    @Query("Update movie set favourite = :favourite where id = :id")
    int updateFavorite(Integer id, Integer favourite);

    @Query("SELECT id FROM movie WHERE id = :id limit 1")
    int checkIfMovieExists(Integer id);

    @Query("Select id from movie where id = :id and favourite != 0")
    int checkIfIsFavourite(Integer id);

    @Query("Delete from movie where id = :id")
    void deleteMovie(Integer id);

    @Query("Select count(*) from movie where type = :type")
    int moviesSize(String type);

    @Query("SELECT count(*) FROM movie , genre WHERE type = :type AND genre_id  = :id AND movie.id  = genre.movie_id")
    int getMoviesSizeWithGenre(String type, Integer id);

    @Query("Select id from movie")
    List<Integer> getAllId();

    @Query("Select id from movie where favourite != 0")
    List<Integer> getAllFavouriteId();

    @Query("Select count(*) from movie where favourite != 0")
    LiveData<Integer> loadAllFavouriteMovies();
}
