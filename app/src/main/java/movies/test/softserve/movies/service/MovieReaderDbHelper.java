package movies.test.softserve.movies.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import movies.test.softserve.movies.db.entity.MovieDbEntities.MovieEntry;

/**
 * Created by rkrit on 27.10.17.
 */

public class MovieReaderDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieEntry.COLUMN_NAME_TITLE + " TEXT," +
                    MovieEntry.COLUMN_NAME_OVERVIEW + " TEXT," +
                    MovieEntry.COLUMN_NAME_IMAGE + " TEXT," +
                    MovieEntry.COLUMN_NAME_VOTE_AVERAGE + " REAL," +
                    MovieEntry.COLUMN_NAME_RELEASE_DATE + " TEXT," +
                    MovieEntry.COLUMN_NAME_VOTE_COUNT + " INTEGER," +
                    MovieEntry.COLUMN_NAME_FAVOURITE + " INTEGER," +
                    MovieEntry.COLUMN_NAME_WATCHED + " INTEGER)";


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movie.db";


    public MovieReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

