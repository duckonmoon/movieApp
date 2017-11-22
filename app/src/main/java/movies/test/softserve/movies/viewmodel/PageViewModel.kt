package movies.test.softserve.movies.viewmodel

import android.arch.lifecycle.ViewModel
import movies.test.softserve.movies.entity.TVEntity

/**
 * Created by rkrit on 06.11.17.
 */
class PageViewModel : ViewModel() {
    var list: List<TVEntity> = ArrayList()
    var page: Int = 1
}