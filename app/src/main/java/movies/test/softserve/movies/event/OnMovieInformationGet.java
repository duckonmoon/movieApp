package movies.test.softserve.movies.event;

import movies.test.softserve.movies.entity.FullMovie;

/**
 * Created by rkrit on 26.10.17.
 */

public interface OnMovieInformationGet {
    void onMovieGet(FullMovie movie);
}
