package movies.test.softserve.movies.fragment

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
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
import movies.test.softserve.movies.service.DbMovieServiceRoom
import movies.test.softserve.movies.util.StartActivityClass

class TVShowFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private var repository: TVShowsRepository = TVShowsRepository.getInstance()
    private var dbService: DbMovieServiceRoom = DbMovieServiceRoom.getInstance()
    private var helperService: DBHelperService = DBHelperService()
    private var handler: Handler = Handler()
    private var listener: OnListOfTVShowsGetListener = object : OnListOfTVShowsGetListener {
        override fun onListOfTVShowsGet() {
            mRecyclerView.adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tvshow_list, container, false)
        mRecyclerView = view as RecyclerView
        val isTablet = resources.getBoolean(R.bool.isTablet)
        if (isTablet) {
            mRecyclerView.layoutManager = GridLayoutManager(context, 2) as RecyclerView.LayoutManager
        } else {
            mRecyclerView.layoutManager = LinearLayoutManager(context)

        }
        view.adapter = MovieListWrapper(MovieRecyclerViewAdapter(repository.tvShows,
                MovieRecyclerViewAdapter.OnMovieSelect { mov ->
                    StartActivityClass.startDetailsActivity(activity, mov)
                }, MovieRecyclerViewAdapter.OnFavouriteClick { movie, position ->
            Thread {
                if (helperService.toDoWithFavourite(movie)) {
                    handler.post({
                        Snackbar.make(view, R.string.added_to_favourite, Snackbar.LENGTH_SHORT)
                                .show()
                        view.adapter.notifyItemChanged(position)
                    })
                } else {
                    handler.post({
                        buildAlertDialog(movie, position)
                    })
                }
            }.start()


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
        mRecyclerView.adapter.notifyDataSetChanged()
        repository.addOnListOfTVShowsGetListener(listener)
    }

    override fun onPause() {
        super.onPause()
        repository.removeOnListOfTVShowsGetListener(listener)
    }

    private fun buildAlertDialog(movie: TVEntity, position: Int) {
        AlertDialog.Builder(activity!!)
                .setMessage(R.string.delete_from_watched)
                .setPositiveButton(R.string.yes) { _, _ ->
                    Thread {
                        dbService.deleteFromDb(movie)
                    }.start()
                    Snackbar.make(mRecyclerView, R.string.mark_unwatched,
                            Snackbar.LENGTH_LONG).show()
                    mRecyclerView.adapter.notifyItemChanged(position)
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    Thread {

                        dbService.cancelFavourite(movie)

                    }.start()
                    Snackbar.make(mRecyclerView, R.string.removed_from_favourite,
                            Snackbar.LENGTH_LONG).show()
                    mRecyclerView.adapter.notifyItemChanged(position)
                }.create()
                .show()
    }
}
