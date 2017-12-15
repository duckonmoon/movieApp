package movies.test.softserve.movies.service;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import movies.test.softserve.movies.dao.GenreDao;
import movies.test.softserve.movies.dao.MovieDao;
import movies.test.softserve.movies.db.entity.*;

/**
 * Created by root on 15.12.17.
 */

@Database(entities = {Movie.class, Genre.class}, version = 1,exportSchema = false)
public abstract class AppRoomDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();
    public abstract GenreDao genreDao();
}
