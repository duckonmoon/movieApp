package movies.test.softserve.movies.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import movies.test.softserve.movies.controller.MainController

/**
 * Created by root on 20.12.17.
 */
abstract class BaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainController.getInstance().addDbObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        MainController.getInstance().removeDbObserver(this)
    }
}