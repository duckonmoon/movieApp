package movies.test.softserve.movies.event;

import movies.test.softserve.movies.entity.FullMovie;

@FunctionalInterface
public interface OnMovieInformationGet {
    void onMovieGet(FullMovie movie);
}
