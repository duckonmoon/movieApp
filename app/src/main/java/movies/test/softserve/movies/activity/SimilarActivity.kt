package movies.test.softserve.movies.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search.*
import movies.test.softserve.movies.R
import movies.test.softserve.movies.adapter.MovieListWrapper
import movies.test.softserve.movies.adapter.MovieRecyclerViewAdapter
import movies.test.softserve.movies.entity.TVEntity
import movies.test.softserve.movies.event.OnSimilarTVEntitiesGetListener
import movies.test.softserve.movies.repository.TVShowsRepository
import movies.test.softserve.movies.service.DBHelperService
import movies.test.softserve.movies.service.DBMovieService
import movies.test.softserve.movies.service.MovieService
import movies.test.softserve.movies.util.StartActivityClass
import java.io.Serializable

class SimilarActivity : BaseActivity() {

    companion object {
        val MOVIE = "Movie"
    }

    private var helperService: DBHelperService = DBHelperService()
    private var dbService: DBMovieService = DBMovieService.getInstance()
    private var tvShowsRepository: TVShowsRepository = TVShowsRepository.getInstance()
    private var movieService: MovieService = MovieService.getInstance()


    private lateinit var movie: TVEntity
    private lateinit var transfer: Transfer

    private var listener: OnSimilarTVEntitiesGetListener = OnSimilarTVEntitiesGetListener { tvEntities ->
        run {
            transfer.list.addAll(tvEntities)
            transfer.page += 1
            recyclerview.adapter.notifyDataSetChanged()
        }
    }

    private val comp: String = "notComp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        movie = intent.extras[MOVIE] as TVEntity
        transfer = Transfer(movie)
        val isTablet = resources.getBoolean(R.bool.isTablet)
        if (isTablet) {
            recyclerview.layoutManager = GridLayoutManager(this, 2)
        } else {
            recyclerview.layoutManager = LinearLayoutManager(this)
        }
        if (savedInstanceState == null) {
            setAdapter()
        }
    }

    private fun tvShowRequest() {
        tvShowsRepository.tryToGetSimilarTvShows(movie.id, transfer.page)
    }

    private fun movieRequest() {
        movieService.tryToGetSimilarMovies(movie.id, transfer.page)
    }


    private fun setAdapter() {
        recyclerview.adapter = MovieListWrapper(MovieRecyclerViewAdapter(transfer.list,
                MovieRecyclerViewAdapter.OnMovieSelect { mov ->
                    StartActivityClass.startDetailsActivity(this, mov)
                },
                MovieRecyclerViewAdapter.OnFavouriteClick { movie ->
                    if (helperService.toDoWithFavourite(movie)) {
                        Snackbar.make(recyclerview, R.string.add_to_favourite, Snackbar.LENGTH_SHORT)
                                .show()
                    } else {
                        buildAlertDialog(movie, recyclerview)
                    }
                    recyclerview.adapter.notifyDataSetChanged()
                }),
                object : MovieListWrapper.OnEndReachListener {
                    override fun onEndReach(): MovieListWrapper.State {
                        if (movie.type == TVEntity.TYPE.MOVIE) {
                            movieRequest()
                        } else {
                            tvShowRequest()
                        }
                        return MovieListWrapper.State.Loading
                    }

                    override fun onEndButtonClick() {

                    }

                })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(comp, transfer)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            transfer = savedInstanceState[comp] as Transfer
        }
        setAdapter()

    }

    override fun onResume() {
        super.onResume()
        movieService.addOnSimilarTVEntitiesGetListener(listener)
        tvShowsRepository.addOnSimilarTVEntitiesGetListener(listener)
    }

    override fun onPause() {
        super.onPause()
        movieService.removeOnSimilarTVEntitiesGetListener(listener)
        tvShowsRepository.removeOnSimilarTVEntitiesGetListener(listener)
    }

    private fun buildAlertDialog(movie: TVEntity, view: RecyclerView) {
        AlertDialog.Builder(this)
                .setMessage(R.string.delete_from_watched)
                .setPositiveButton(R.string.yes) { _, _ ->
                    dbService.deleteFromDb(movie.id)
                    Snackbar.make(view, R.string.mark_unwatched,
                            Snackbar.LENGTH_LONG).show()
                    view.adapter.notifyDataSetChanged()
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    dbService.cancelFavourite(movie.id)
                    Snackbar.make(view, R.string.removed_from_favourite,
                            Snackbar.LENGTH_LONG).show()
                    view.adapter.notifyDataSetChanged()
                }.create()
                .show()
    }

    private class Transfer(var movie: TVEntity) : Serializable {
        var page: Int = 1
        var list: ArrayList<TVEntity> = ArrayList()
    }
}
