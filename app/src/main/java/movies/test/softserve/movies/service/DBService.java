package movies.test.softserve.movies.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.controllers.MainController;
import movies.test.softserve.movies.db.entity.MovieDbEntities.MovieEntry;
import movies.test.softserve.movies.entity.Movie;

/**
 * Created by rkrit on 27.10.17.
 */

public class DBService {
    private static DBService INSTANCE;
    private SQLiteDatabase database;




    public static DBService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DBService();
        }
        return INSTANCE;
    }

    private DBService() {
        database = MainController.getInstance().getDatabase();
    }

    public long insertMovie(Integer id, String title, float voteAverage, int voteCount, String overview, String releaseDate,String posterpath) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, id);
        values.put(MovieEntry.COLUMN_NAME_TITLE, title);
        values.put(MovieEntry.COLUMN_NAME_VOTE_AVERAGE, voteAverage);
        values.put(MovieEntry.COLUMN_NAME_VOTE_COUNT, voteCount);
        values.put(MovieEntry.COLUMN_NAME_OVERVIEW, overview);
        values.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, releaseDate);
        values.put(MovieEntry.COLUMN_NAME_IMAGE,posterpath);
        values.put(MovieEntry.COLUMN_NAME_FAVOURITE,1);
        values.put(MovieEntry.COLUMN_NAME_WATCHED,1);
        return database.insert(MovieEntry.TABLE_NAME, null, values);
    }

    public boolean checkIfMovieExists(Integer id) {
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

    public boolean checkIfMovieIsFavourite(Integer id){
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

    public boolean deleteMovieFromDb(Integer id) {
        return database.delete(MovieEntry.TABLE_NAME, MovieEntry._ID + " = " + id, null) > 0;
    }

    public List<Movie> getAllMovies(){
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
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
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

    public List<Movie> getFavouriteMovies(){
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
                MovieEntry.COLUMN_NAME_FAVOURITE + " > 0",
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
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
}
