package movies.test.softserve.movies.viewmodel

import android.arch.lifecycle.ViewModel
import movies.test.softserve.movies.entity.Backdrop
import movies.test.softserve.movies.entity.FullMovie
import movies.test.softserve.movies.entity.Poster

/**
 * Created by rkrit on 02.11.17.
 */
class FullMovieViewModel : ViewModel() {
    var fullMovie: FullMovie? = null
    var posters :List<Poster>? = null
    var backdrops : List<Backdrop>? = null
}