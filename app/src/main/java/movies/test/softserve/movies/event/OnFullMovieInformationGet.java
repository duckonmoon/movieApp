package movies.test.softserve.movies.event;

import movies.test.softserve.movies.db.entity.MovieFirebaseDTO;
import movies.test.softserve.movies.entity.FullMovie;

/**
 * Created by root on 29.12.17.
 */

public interface OnFullMovieInformationGet {
    void onMovieGet(FullMovie movie, MovieFirebaseDTO movieFirebaseDTO);
}
