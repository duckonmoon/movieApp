package movies.test.softserve.movies.viewmodel

import android.arch.lifecycle.ViewModel
import movies.test.softserve.movies.entity.Movie
import movies.test.softserve.movies.service.MovieService

/**
 * Created by rkrit on 06.11.17.
 */
class PageViewModel : ViewModel() {
    var list: List<Movie> = ArrayList()
    var page: Int = 1
}