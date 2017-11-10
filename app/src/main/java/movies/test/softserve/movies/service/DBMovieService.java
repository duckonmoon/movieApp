package movies.test.softserve.movies.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.controller.MainController;
import movies.test.softserve.movies.db.entity.MovieDbEntities.MovieEntry;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.entity.TVShow;

/**
 * Created by rkrit on 27.10.17.
 */

public class DBMovieService {
    private static DBMovieService INSTANCE;
    private SQLiteDatabase database;

    public static final Integer CONTENT_TYPE_MOVIE = 1;
    public static final Integer CONTENT_TYPE_TVSHOW = 0;

    public static DBMovieService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DBMovieService();
        }
        return INSTANCE;
    }

    private DBMovieService() {
        database = MainController.getInstance().getDatabase();
    }

    public long insertMovieToFavourite(Integer id, String title, float voteAverage, int voteCount, String overview, String releaseDate, String posterpath) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, id);
        values.put(MovieEntry.COLUMN_NAME_TITLE, title);
        values.put(MovieEntry.COLUMN_NAME_VOTE_AVERAGE, voteAverage);
        values.put(MovieEntry.COLUMN_NAME_VOTE_COUNT, voteCount);
        values.put(MovieEntry.COLUMN_NAME_OVERVIEW, overview);
        values.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, releaseDate);
        values.put(MovieEntry.COLUMN_NAME_IMAGE, posterpath);
        values.put(MovieEntry.COLUMN_NAME_FAVOURITE, 1);
        values.put(MovieEntry.COLUMN_NAME_WATCHED, 1);
        values.put(MovieEntry.COLUMN_NAME_TYPE, CONTENT_TYPE_MOVIE);
        return database.insert(MovieEntry.TABLE_NAME, null, values);
    }

    public long insertTVShowToFavourite(Integer id, String name, float voteAverage, int voteCount, String overview, String posterpath) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, id);
        values.put(MovieEntry.COLUMN_NAME_TITLE, name);
        values.put(MovieEntry.COLUMN_NAME_VOTE_AVERAGE, voteAverage);
        values.put(MovieEntry.COLUMN_NAME_VOTE_COUNT, voteCount);
        values.put(MovieEntry.COLUMN_NAME_OVERVIEW, overview);
        values.put(MovieEntry.COLUMN_NAME_IMAGE, posterpath);
        values.put(MovieEntry.COLUMN_NAME_FAVOURITE, 1);
        values.put(MovieEntry.COLUMN_NAME_WATCHED, 1);
        values.put(MovieEntry.COLUMN_NAME_TYPE, CONTENT_TYPE_TVSHOW);
        return database.insert(MovieEntry.TABLE_NAME, null, values);
    }

    public long addMovieToDb(Integer id, String title, float voteAverage, int voteCount, String overview, String releaseDate, String posterpath) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, id);
        values.put(MovieEntry.COLUMN_NAME_TITLE, title);
        values.put(MovieEntry.COLUMN_NAME_VOTE_AVERAGE, voteAverage);
        values.put(MovieEntry.COLUMN_NAME_VOTE_COUNT, voteCount);
        values.put(MovieEntry.COLUMN_NAME_OVERVIEW, overview);
        values.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, releaseDate);
        values.put(MovieEntry.COLUMN_NAME_IMAGE, posterpath);
        values.put(MovieEntry.COLUMN_NAME_FAVOURITE, 0);
        values.put(MovieEntry.COLUMN_NAME_WATCHED, 1);
        values.put(MovieEntry.COLUMN_NAME_TYPE, CONTENT_TYPE_MOVIE);
        return database.insert(MovieEntry.TABLE_NAME, null, values);
    }

    public long addTVShowToDb(Integer id, String name, float voteAverage, int voteCount, String overview, String posterpath) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, id);
        values.put(MovieEntry.COLUMN_NAME_TITLE, name);
        values.put(MovieEntry.COLUMN_NAME_VOTE_AVERAGE, voteAverage);
        values.put(MovieEntry.COLUMN_NAME_VOTE_COUNT, voteCount);
        values.put(MovieEntry.COLUMN_NAME_OVERVIEW, overview);
        values.put(MovieEntry.COLUMN_NAME_IMAGE, posterpath);
        values.put(MovieEntry.COLUMN_NAME_FAVOURITE, 0);
        values.put(MovieEntry.COLUMN_NAME_WATCHED, 1);
        values.put(MovieEntry.COLUMN_NAME_TYPE, CONTENT_TYPE_TVSHOW);
        return database.insert(MovieEntry.TABLE_NAME, null, values);
    }


    public boolean checkIfExists(Integer id) {
        String[] projection = {
                MovieEntry._ID
        };
        String[] sA = {id.toString()};
        Cursor cursor = database.query(
                MovieEntry.TABLE_NAME,
                projection,
                MovieEntry._ID + " LIKE ?",
                sA,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID));
            return true;
        }
        return false;
    }

    public boolean checkIfIsFavourite(Integer id) {
        String[] projection = {
                MovieEntry._ID
        };
        String[] sA = {id.toString()};
        Cursor cursor = database.query(
                MovieEntry.TABLE_NAME,
                projection,
                MovieEntry._ID + " LIKE ? AND " + MovieEntry.COLUMN_NAME_FAVOURITE + " > 0",
                sA,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID));
            return true;
        }
        return false;
    }

    public boolean deleteFromDb(Integer id) {
        return database.delete(MovieEntry.TABLE_NAME, MovieEntry._ID + " = " + id, null) > 0;
    }

    public List<Movie> getAllMovies() {
        ArrayList<Movie> movieArrayList = new ArrayList<>();
        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_NAME_VOTE_COUNT,
                MovieEntry.COLUMN_NAME_VOTE_AVERAGE,
                MovieEntry.COLUMN_NAME_RELEASE_DATE,
                MovieEntry.COLUMN_NAME_IMAGE,
                MovieEntry.COLUMN_NAME_TITLE,
                MovieEntry.COLUMN_NAME_OVERVIEW,
                MovieEntry.COLUMN_NAME_WATCHED,
                MovieEntry.COLUMN_NAME_FAVOURITE,
                MovieEntry.COLUMN_NAME_TYPE
        };
        Cursor cursor = database.query(
                MovieEntry.TABLE_NAME,
                projection,
                MovieEntry.COLUMN_NAME_TYPE + " > 0",
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            movie.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_RELEASE_DATE)));
            movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
            movieArrayList.add(movie);
        }
        return movieArrayList;
    }

    public List<Movie> getFavouriteMovies() {
        ArrayList<Movie> movieArrayList = new ArrayList<>();
        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_NAME_VOTE_COUNT,
                MovieEntry.COLUMN_NAME_VOTE_AVERAGE,
                MovieEntry.COLUMN_NAME_RELEASE_DATE,
                MovieEntry.COLUMN_NAME_IMAGE,
                MovieEntry.COLUMN_NAME_TITLE,
                MovieEntry.COLUMN_NAME_OVERVIEW,
                MovieEntry.COLUMN_NAME_WATCHED,
                MovieEntry.COLUMN_NAME_FAVOURITE
        };
        Cursor cursor = database.query(
                MovieEntry.TABLE_NAME,
                projection,
                MovieEntry.COLUMN_NAME_FAVOURITE + " > 0 AND " + MovieEntry.COLUMN_NAME_TYPE + " > 0",
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            movie.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_RELEASE_DATE)));
            movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
            movieArrayList.add(movie);
        }
        return movieArrayList;

    }

    public Movie getMovieByID(Integer id) {
        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_NAME_VOTE_COUNT,
                MovieEntry.COLUMN_NAME_VOTE_AVERAGE,
                MovieEntry.COLUMN_NAME_RELEASE_DATE,
                MovieEntry.COLUMN_NAME_IMAGE,
                MovieEntry.COLUMN_NAME_TITLE,
                MovieEntry.COLUMN_NAME_OVERVIEW,
                MovieEntry.COLUMN_NAME_WATCHED,
                MovieEntry.COLUMN_NAME_FAVOURITE
        };
        Cursor cursor = database.query(
                MovieEntry.TABLE_NAME,
                projection,
                MovieEntry._ID + " = " + id,
                null,
                null,
                null,
                null
        );
        Movie movie = new Movie();
        if (cursor.moveToNext()) {
            movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            movie.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_RELEASE_DATE)));
            movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
        }
        return movie;
    }

    public List<TVShow> getAllTVShows() {
        ArrayList<TVShow> movieArrayList = new ArrayList<>();
        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_NAME_VOTE_COUNT,
                MovieEntry.COLUMN_NAME_VOTE_AVERAGE,
                MovieEntry.COLUMN_NAME_RELEASE_DATE,
                MovieEntry.COLUMN_NAME_IMAGE,
                MovieEntry.COLUMN_NAME_TITLE,
                MovieEntry.COLUMN_NAME_OVERVIEW,
                MovieEntry.COLUMN_NAME_WATCHED,
                MovieEntry.COLUMN_NAME_FAVOURITE,
                MovieEntry.COLUMN_NAME_TYPE
        };
        Cursor cursor = database.query(
                MovieEntry.TABLE_NAME,
                projection,
                MovieEntry.COLUMN_NAME_TYPE + " = 0",
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            TVShow movie = new TVShow();
            movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            movie.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            movie.setName(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
            movieArrayList.add(movie);
        }
        return movieArrayList;
    }

    public List<TVShow> getFavouriteTVShows() {
        ArrayList<TVShow> movieArrayList = new ArrayList<>();
        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_NAME_VOTE_COUNT,
                MovieEntry.COLUMN_NAME_VOTE_AVERAGE,
                MovieEntry.COLUMN_NAME_RELEASE_DATE,
                MovieEntry.COLUMN_NAME_IMAGE,
                MovieEntry.COLUMN_NAME_TITLE,
                MovieEntry.COLUMN_NAME_OVERVIEW,
                MovieEntry.COLUMN_NAME_WATCHED,
                MovieEntry.COLUMN_NAME_FAVOURITE
        };
        Cursor cursor = database.query(
                MovieEntry.TABLE_NAME,
                projection,
                MovieEntry.COLUMN_NAME_FAVOURITE + " > 0 AND " + MovieEntry.COLUMN_NAME_TYPE + " = 0",
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            TVShow tvShow = new TVShow();
            tvShow.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            tvShow.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            tvShow.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            tvShow.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            tvShow.setName(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            tvShow.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
            movieArrayList.add(tvShow);
        }
        return movieArrayList;

    }

    public TVShow getFavouriteTVShowByID(Integer id) {
        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_NAME_VOTE_COUNT,
                MovieEntry.COLUMN_NAME_VOTE_AVERAGE,
                MovieEntry.COLUMN_NAME_RELEASE_DATE,
                MovieEntry.COLUMN_NAME_IMAGE,
                MovieEntry.COLUMN_NAME_TITLE,
                MovieEntry.COLUMN_NAME_OVERVIEW,
                MovieEntry.COLUMN_NAME_WATCHED,
                MovieEntry.COLUMN_NAME_FAVOURITE
        };
        Cursor cursor = database.query(
                MovieEntry.TABLE_NAME,
                projection,
                MovieEntry._ID + " = " + id,
                null,
                null,
                null,
                null
        );
        TVShow tvShow = new TVShow();
        if (cursor.moveToNext()) {
            tvShow.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            tvShow.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            tvShow.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            tvShow.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            tvShow.setName(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            tvShow.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
        }
        return tvShow;
    }



    public void setFavourite(Integer id) {
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_NAME_FAVOURITE, 1);
        database.update(MovieEntry.TABLE_NAME,cv,MovieEntry._ID+ "= " + id, null);
    }

    public void cancelFavourite(Integer id) {
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_NAME_FAVOURITE, 0);
        database.update(MovieEntry.TABLE_NAME,cv,MovieEntry._ID+ "= " + id, null);
    }
}
