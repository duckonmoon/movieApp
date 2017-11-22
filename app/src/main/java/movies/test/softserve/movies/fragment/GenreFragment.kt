package movies.test.softserve.movies.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import movies.test.softserve.movies.R
import movies.test.softserve.movies.adapter.ViewAdapter
import movies.test.softserve.movies.controller.MainController
import movies.test.softserve.movies.entity.Genre
import movies.test.softserve.movies.event.OnListOfGenresGetListener
import movies.test.softserve.movies.service.MovieService
import movies.test.softserve.movies.service.StartActivityClass

class GenreFragment : Fragment() {

    private var mRecyclerView: RecyclerView? = null
    private var onListOfGenresGetListener: OnListOfGenresGetListener? = null

    private var mainController : MainController = MainController.getInstance()
    private var movieService : MovieService = MovieService.getInstance()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_genre_list, container, false)
        mRecyclerView = view as RecyclerView
        val context = view.getContext()
        view.layoutManager = LinearLayoutManager(context)
        view.adapter = ViewAdapter(MainController.getInstance().genres, onItemClickListener = object : ViewAdapter.OnItemClickListener {
            override fun onItemClick(genre: Genre) {
                StartActivityClass.startActivitySearch(activity, genre)
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        if (onListOfGenresGetListener == null) {
            onListOfGenresGetListener = object : OnListOfGenresGetListener {
                override fun onListOfGenresGet(genres: List<Genre>?) {
                    if (mainController.genres.size > 0) {
                        return
                    } else {
                        mainController.genres.addAll(genres!!)
                        mRecyclerView!!.adapter.notifyDataSetChanged()
                    }
                }
            }
            if (mainController.genres.size < 1) {
                movieService.tryToGetAllGenres()
            }
        }
        movieService.addOnListOfGenresGetListener(onListOfGenresGetListener!!)
    }

    override fun onPause() {
        super.onPause()
        movieService.removeOnListOfGenresGetListener(onListOfGenresGetListener!!)
        onListOfGenresGetListener = null
    }
}
