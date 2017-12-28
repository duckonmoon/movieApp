package movies.test.softserve.movies.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import java.util.List;

import movies.test.softserve.movies.db.entity.Genre;

/**
 * Created by root on 15.12.17.
 */

@Dao
public interface GenreDao {
    @Insert
    void insertGenres(List<Genre> genres);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGenres(Genre genre);
}
