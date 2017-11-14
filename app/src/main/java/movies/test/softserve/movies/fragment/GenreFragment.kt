package movies.test.softserve.movies.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import movies.test.softserve.movies.R
import movies.test.softserve.movies.activity.SearchActivity
import movies.test.softserve.movies.adapter.ViewAdapter
import movies.test.softserve.movies.controller.MainController
import movies.test.softserve.movies.entity.Genre
import movies.test.softserve.movies.event.OnListOfGenresGetListener
import movies.test.softserve.movies.service.MovieService

class GenreFragment : Fragment() {

    private var mRecyclerView: RecyclerView? = null
    private var onListOfGenresGetListener: OnListOfGenresGetListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_genre_list, container, false)

        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = ViewAdapter(MainController.getInstance().genres, onItemClickListener = object : ViewAdapter.OnItemClickListener {
                override fun onItemClick(genre: Genre) {
                    val intent = Intent(activity, SearchActivity::class.java)
                    intent.putExtra(SearchActivity.SEARCH_PARAM, SearchActivity.GENRES)
                    intent.putExtra(SearchActivity.ID, genre.id)
                    intent.putExtra(SearchActivity.NAME, genre.name)
                    startActivity(intent)
                }
            })
            mRecyclerView = view
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (onListOfGenresGetListener == null) {
            onListOfGenresGetListener = object : OnListOfGenresGetListener {
                override fun onListOfGenresGet(genres: List<Genre>?) {
                    if (MainController.getInstance().genres.size > 0) {
                        return
                    } else {
                        MainController.getInstance().genres.addAll(genres!!)
                        mRecyclerView!!.adapter.notifyDataSetChanged()
                    }
                }
            }
            if (MainController.getInstance().genres.size < 1){
                MovieService.getInstance().tryToGetAllGenres()
            }
        }
        MovieService.getInstance().addOnListOfGenresGetListener(onListOfGenresGetListener!!)
    }

    override fun onPause() {
        super.onPause()
        MovieService.getInstance().removeOnListOfGenresGetListener(onListOfGenresGetListener!!)
        onListOfGenresGetListener = null
    }
}
