package movies.test.softserve.movies.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import movies.test.softserve.movies.R
import movies.test.softserve.movies.adapter.MyMovieListWrapper
import movies.test.softserve.movies.adapter.MyTVShowRecyclerViewAdapter
import movies.test.softserve.movies.entity.TVShow
import movies.test.softserve.movies.event.OnListOfTVShowsGetListener
import movies.test.softserve.movies.repository.TVShowsRepository

class TVShowFragment : Fragment() {

    var mRecyclerView: RecyclerView? = null
    var repository: TVShowsRepository = TVShowsRepository.getInstance()
    var listener: OnListOfTVShowsGetListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_tvshow_list, container, false)
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = MyMovieListWrapper(MyTVShowRecyclerViewAdapter(repository.tvShows,
                    object : MyTVShowRecyclerViewAdapter.OnMovieSelect {
                        override fun OnMovieSelected(tvShow: TVShow) {
                        }
                    }, object : MyTVShowRecyclerViewAdapter.OnFavouriteClick {
                override fun onFavouriteClick(tvShow: TVShow) {
                }
            }), MyMovieListWrapper.OnEndReachListener { repository.tryToGetTVShows() })
            mRecyclerView = view
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        mRecyclerView!!.adapter.notifyDataSetChanged()
        if (listener == null) {
            listener = object : OnListOfTVShowsGetListener {
                override fun onListOfTVShowsGet(tvShows: List<TVShow>) {
                    mRecyclerView!!.adapter.notifyDataSetChanged()
                }
            }
            repository.addOnListOfTVShowsGetListener(listener)
        }
    }

    override fun onPause() {
        super.onPause()
        repository.removeOnListOfTVShowsGetListener(listener)
        listener = null
    }
}
