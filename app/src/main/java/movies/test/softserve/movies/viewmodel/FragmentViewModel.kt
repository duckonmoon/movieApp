package movies.test.softserve.movies.viewmodel

import android.arch.lifecycle.ViewModel
import android.view.MenuItem
import movies.test.softserve.movies.fragment.GenreFragment
import movies.test.softserve.movies.fragment.MovieFragment
import movies.test.softserve.movies.fragment.TVShowFragment
import movies.test.softserve.movies.fragment.WatchedFragment

/**
 * Created by rkrit on 02.11.17.
 */
class FragmentViewModel : ViewModel() {
    var movieFragment: MovieFragment? = null
    var watchedFragment: WatchedFragment? = null
    var genresFragment: GenreFragment? = null
    var tvShowFragment: TVShowFragment? = null
}