package movies.test.softserve.movies.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import movies.test.softserve.movies.R
import movies.test.softserve.movies.adapter.AchievementsRecyclerViewAdapter
import movies.test.softserve.movies.entity.Achievement

class AchievementsFragment : Fragment() {
    private var mColumnCount = 3

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_achievements_list, container, false)

        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = GridLayoutManager(context, mColumnCount)
            view.adapter = AchievementsRecyclerViewAdapter(Achievement.getAchievements())
        }
        return view
    }
}
