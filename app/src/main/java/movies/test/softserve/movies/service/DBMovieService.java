package movies.test.softserve.movies.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.controller.MainController;
import movies.test.softserve.movies.db.entity.MovieDbEntities.GenreEntry;
import movies.test.softserve.movies.db.entity.MovieDbEntities.MovieEntry;
import movies.test.softserve.movies.entity.TVEntity;

/**
 * Created by rkrit on 27.10.17.
 */

public class DBMovieService {
    private static DBMovieService INSTANCE;
    private SQLiteDatabase database;
    private RatingService ratingService = RatingService.getInstance();

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

    public long insertMovieToFavourite(Integer id, String title, float voteAverage, int voteCount, String overview, String releaseDate, String posterpath, List<Integer> genres) {
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
        ratingService.change(voteAverage,RatingService.ADD);
        insertGenresIfNotExists(id,genres);
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
        ratingService.change(voteAverage,RatingService.ADD);
        return database.insert(MovieEntry.TABLE_NAME, null, values);
    }

    public long addMovieToDb(Integer id, String title, float voteAverage, int voteCount, String overview, String releaseDate, String posterpath,List<Integer> genres) {
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
        ratingService.change(voteAverage,RatingService.ADD);
        insertGenresIfNotExists(id,genres);
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
        ratingService.change(voteAverage,RatingService.ADD);
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
        if (cursor.moveToNext()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID));
            cursor.close();
            return true;
        }
        cursor.close();
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
        if (cursor.moveToNext()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID));
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public boolean deleteFromDb(Integer id) {
        TVEntity movie = getMovieByID(id);
        ratingService.change(movie.getVoteAverage().floatValue(),RatingService.SUB);
        database.delete(GenreEntry.TABLE_NAME, GenreEntry.COLUMN_NAME_MOVIE_ID + " = " + id, null);
        return database.delete(MovieEntry.TABLE_NAME, MovieEntry._ID + " = " + id, null) > 0;
    }

    public List<TVEntity> getAllMovies() {
        ArrayList<TVEntity> movieArrayList = new ArrayList<>();
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
            TVEntity movie = new TVEntity();
            movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            movie.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_RELEASE_DATE)));
            movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
            movie.setType(TVEntity.TYPE.MOVIE);
            movieArrayList.add(movie);
        }
        cursor.close();
        return movieArrayList;
    }

    public List<TVEntity> getFavouriteMovies() {
        ArrayList<TVEntity> movieArrayList = new ArrayList<>();
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
            TVEntity movie = new TVEntity();
            movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            movie.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_RELEASE_DATE)));
            movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
            movie.setGenreIds(getMovieGenres(movie.getId()));
            movie.setType(TVEntity.TYPE.MOVIE);
            movieArrayList.add(movie);
        }
        cursor.close();
        return movieArrayList;

    }

    public TVEntity getMovieByID(Integer id) {
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
        TVEntity movie = new TVEntity();
        if (cursor.moveToNext()) {
            movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            movie.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_RELEASE_DATE)));
            movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
            movie.setType(TVEntity.TYPE.MOVIE);
        }
        cursor.close();
        return movie;
    }

    public List<TVEntity> getAllTVShows() {
        ArrayList<TVEntity> movieArrayList = new ArrayList<>();
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
            TVEntity movie = new TVEntity();
            movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            movie.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
            movie.setType(TVEntity.TYPE.TV_SHOW);
            movieArrayList.add(movie);
        }
        cursor.close();
        return movieArrayList;
    }

    public List<TVEntity> getFavouriteTVShows() {
        ArrayList<TVEntity> movieArrayList = new ArrayList<>();
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
            TVEntity tvShow = new TVEntity();
            tvShow.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            tvShow.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            tvShow.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            tvShow.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            tvShow.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            tvShow.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
            tvShow.setType(TVEntity.TYPE.TV_SHOW);
            movieArrayList.add(tvShow);
        }
        cursor.close();
        return movieArrayList;

    }

    public TVEntity getFavouriteTVShowByID(Integer id) {
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
        TVEntity tvShow = new TVEntity();
        if (cursor.moveToNext()) {
            tvShow.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry._ID)));
            tvShow.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_COUNT)));
            tvShow.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_IMAGE)));
            tvShow.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_OVERVIEW)));
            tvShow.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)));
            tvShow.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
            tvShow.setType(TVEntity.TYPE.TV_SHOW);
        }
        cursor.close();
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

    public int getMoviesSize(){
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " + MovieEntry.TABLE_NAME + " WHERE " +  MovieEntry.COLUMN_NAME_TYPE + " = " +  CONTENT_TYPE_MOVIE, null);
        cursor.moveToFirst();
        int size = cursor.getInt(0);
        cursor.close();
        return size;
    }

    public int getTVShowsSize(){
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " + MovieEntry.TABLE_NAME + " WHERE " +  MovieEntry.COLUMN_NAME_TYPE + " = " +  CONTENT_TYPE_TVSHOW, null);
        cursor.moveToFirst();
        int size = cursor.getInt(0);
        cursor.close();
        return size;
    }

    public int getMoviesSizeWithGenre(Integer genre){
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " + MovieEntry.TABLE_NAME + ", " + GenreEntry.TABLE_NAME
                + " WHERE " +  MovieEntry.COLUMN_NAME_TYPE + " = " +  CONTENT_TYPE_MOVIE
                + " AND " + GenreEntry.TABLE_NAME + "." + GenreEntry._ID + " = " +  genre
                + " AND " + MovieEntry.TABLE_NAME + "." + MovieEntry._ID + " = " + GenreEntry.COLUMN_NAME_MOVIE_ID, null);
        cursor.moveToFirst();
        int size = cursor.getInt(0);
        cursor.close();
        return size;
    }

    private void insertGenresIfNotExists(Integer id_movie,List<Integer> genres) {
        for (Integer genre:
             genres) {
            ContentValues cv = new ContentValues();
            cv.put(GenreEntry._ID, genre);
            cv.put(GenreEntry.COLUMN_NAME_MOVIE_ID, id_movie);
            long i = database.insert(GenreEntry.TABLE_NAME,null,cv);
            Log.w("SQL", "" + i);
        }
    }

    private List<Integer> getMovieGenres(Integer id) {
        ArrayList<Integer> genres = new ArrayList<>();
        String[] projection = { GenreEntry._ID};
        Cursor cursor = database.query(
                GenreEntry.TABLE_NAME,
                projection,
                GenreEntry.COLUMN_NAME_MOVIE_ID + " = " + id,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            genres.add(cursor.getColumnIndexOrThrow(GenreEntry._ID));
        }
        cursor.close();
        return genres;
    }

}
