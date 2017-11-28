package movies.test.softserve.movies.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import movies.test.softserve.movies.R
import movies.test.softserve.movies.adapter.MovieListWrapper
import movies.test.softserve.movies.adapter.MovieRecyclerViewAdapter
import movies.test.softserve.movies.entity.TVEntity
import movies.test.softserve.movies.event.OnListOfTVShowsGetListener
import movies.test.softserve.movies.repository.TVShowsRepository
import movies.test.softserve.movies.service.DBHelperService
import movies.test.softserve.movies.service.DBMovieService
import movies.test.softserve.movies.util.StartActivityClass

class TVShowFragment : Fragment() {

    private var mRecyclerView: RecyclerView? = null
    private var repository: TVShowsRepository = TVShowsRepository.getInstance()
    private var dbService: DBMovieService = DBMovieService.getInstance()
    private var helperService: DBHelperService = DBHelperService()
    private var listener: OnListOfTVShowsGetListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_tvshow_list, container, false)
        mRecyclerView = view as RecyclerView
        view.layoutManager = LinearLayoutManager(view.context)
        view.adapter = MovieListWrapper(MovieRecyclerViewAdapter(repository.tvShows,
                MovieRecyclerViewAdapter.OnMovieSelect { mov ->
                    StartActivityClass.startDetailsActivity(activity, mov)
                }, MovieRecyclerViewAdapter.OnFavouriteClick { movie ->

            if (helperService.toDoWithFavourite(movie)) {
                Snackbar.make(view, "Added to favourite", Snackbar.LENGTH_SHORT)
                        .show()
            } else {
                buildAlertDialog(movie)
            }

            view.adapter.notifyDataSetChanged()
        }),
                object : MovieListWrapper.OnEndReachListener {
                    override fun onEndReach(): MovieListWrapper.State {
                        repository.tryToGetTVShows()
                        return MovieListWrapper.State.Loading
                    }

                    override fun onEndButtonClick() {

                    }
                })
        return view
    }

    override fun onResume() {
        super.onResume()
        mRecyclerView!!.adapter.notifyDataSetChanged()
        if (listener == null) {
            listener = object : OnListOfTVShowsGetListener {
                override fun onListOfTVShowsGet() {
                    mRecyclerView!!.adapter.notifyDataSetChanged()
                }
            }
            repository.addOnListOfTVShowsGetListener(listener)
        }
    }

    override fun onPause() {
        super.onPause()
        if (listener != null) {
            repository.removeOnListOfTVShowsGetListener(listener)
            listener = null
        }
    }

    private fun buildAlertDialog(movie: TVEntity) {
        AlertDialog.Builder(activity)
                .setMessage(R.string.delete_from_watched)
                .setPositiveButton(R.string.yes) { _, _ ->
                    dbService.deleteFromDb(movie.id)
                    Snackbar.make(mRecyclerView!!, "Deleted from favourite",
                            Snackbar.LENGTH_LONG).show()
                    mRecyclerView!!.adapter.notifyDataSetChanged()
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    dbService.cancelFavourite(movie.id)
                    Snackbar.make(mRecyclerView!!, "Deleted from favourite",
                            Snackbar.LENGTH_LONG).show()
                    mRecyclerView!!.adapter.notifyDataSetChanged()
                }.create()
                .show()
    }
}
