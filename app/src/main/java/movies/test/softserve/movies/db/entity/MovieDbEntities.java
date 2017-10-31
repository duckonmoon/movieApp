package movies.test.softserve.movies.db.entity;

import android.provider.BaseColumns;

/**
 * Created by rkrit on 27.10.17.
 */

public final class MovieDbEntities {

    private MovieDbEntities() {
    }

    public static class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_NAME_VOTE_COUNT = "vote_count";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        //TODO add watched and favorite as different movie types
        public static final String COLUMN_NAME_WATCHED = "watched";
        public static final String COLUMN_NAME_FAVOURITE = "favourite";
    }

}
