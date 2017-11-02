package movies.test.softserve.movies.viewmodel;

import android.arch.lifecycle.ViewModel;

import movies.test.softserve.movies.fragment.MovieFragment;
import movies.test.softserve.movies.fragment.WatchedFragment;

/**
 * Created by rkrit on 01.11.17.
 */

public class FragmentViewModel extends ViewModel {
    private MovieFragment movieFragment;
    private WatchedFragment watchedFragment;


    public MovieFragment getMovieFragment() {
        return movieFragment;
    }

    public void setMovieFragment(MovieFragment movieFragment) {
        this.movieFragment = movieFragment;
    }

    public WatchedFragment getWatchedFragment() {
        return watchedFragment;
    }

    public void setWatchedFragment(WatchedFragment watchedFragment) {
        this.watchedFragment = watchedFragment;
    }
}
