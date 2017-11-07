package movies.test.softserve.movies.viewmodel

import android.arch.lifecycle.ViewModel
import movies.test.softserve.movies.entity.Genre

/**
 * Created by rkrit on 07.11.17.
 */
class GenresViewModel : ViewModel(){
    var genres : ArrayList<Genre> = ArrayList()
}