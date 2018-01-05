package movies.test.softserve.movies.viewmodel

import android.arch.lifecycle.ViewModel
import movies.test.softserve.movies.PersonalCabinetFragment
import movies.test.softserve.movies.fragment.*

/**
 * Created by rkrit on 02.11.17.
 */
class FragmentViewModel : ViewModel() {
    var movieFragment: MovieFragment? = null
    var watchedFragment: WatchedFragment? = null
    var genresFragment: GenreFragment? = null
    var tvShowFragment: TVShowFragment? = null
    var searchFragment: SearchFragment? = null
    var achievementsFragment: AchievementsFragment? = null
    var personalCabinetFragment: PersonalCabinetFragment? = null
}