package movies.test.softserve.movies.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import movies.test.softserve.movies.db.entity.Movie;

/**
 * Created by root on 15.12.17.
 */

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie... movies);

    @Query("Select * from movie")
    List<Movie> loadAllMovies();

    @Query("SELECT id FROM movie WHERE id = :id limit 1")
    int checkIfMovieExists(Integer id);

    @Query("Select id from movie where id = :id and favourite != 0")
    int checkIfIsFavourite(Integer id);

    @Delete
    void deleteMovie(Movie ... movies);
}
