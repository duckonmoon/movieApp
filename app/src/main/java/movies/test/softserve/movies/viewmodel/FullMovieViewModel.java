package movies.test.softserve.movies.viewmodel;

import android.arch.lifecycle.ViewModel;

import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.Movie;

/**
 * Created by rkrit on 27.10.17.
 */

public class FullMovieViewModel extends ViewModel {

    private Movie movie;

    private FullMovie fullMovie;

    public FullMovie getFullMovie() {
        return fullMovie;
    }

    public void setFullMovie(FullMovie fullMovie) {
        this.fullMovie = fullMovie;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
