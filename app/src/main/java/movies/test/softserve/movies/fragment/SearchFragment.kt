package movies.test.softserve.movies.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Switch
import kotlinx.android.synthetic.main.fragment_tventity_list.view.*
import movies.test.softserve.movies.R
import movies.test.softserve.movies.adapter.MovieListWrapper
import movies.test.softserve.movies.adapter.MovieRecyclerViewAdapter
import movies.test.softserve.movies.entity.*
import movies.test.softserve.movies.repository.TVShowsRepository
import movies.test.softserve.movies.service.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : Fragment() {

    private var service: MovieService = MovieService.getInstance()
    private var repository: TVShowsRepository = TVShowsRepository.getInstance()
    private var dbService: DBMovieService = DBMovieService.getInstance()
    private var helperService: DBHelperService = DBHelperService()


    private var mRecyclerView: RecyclerView? = null

    //TODO do smth with that
    companion object {
        private val TYPE_TV_SHOW = true
        private val TYPE_MOVIE = false
        private var message: String? = null
        private var query: String = ""
        private var page: Int = 1
        private var type: Boolean = false
        private var list: ArrayList<TVEntity> = ArrayList()
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_tventity_list, container, false)

        val context = view.context
        view.container.setOnClickListener { }
        mRecyclerView = view.list
        val editSearchView: EditText = view.edit_query
        val switchView: Switch = view.switcher

        view.search.setOnClickListener {
            if (editSearchView.text.toString() == "") {
                return@setOnClickListener
            }
            val currView = activity.currentFocus
            if (currView != null) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.windowToken, 0)
            }
            page = 1
            list.clear()
            query = editSearchView.text.toString()
            type = switchView.isChecked
            message = null
            if (type == TYPE_MOVIE) {
                movieRequest()
            } else {
                tvShowRequest()
            }
        }

        view.list.layoutManager = LinearLayoutManager(context)
        view.list.adapter = MovieListWrapper(MovieRecyclerViewAdapter(list, MovieRecyclerViewAdapter.OnMovieSelect { mov ->
            StartActivityClass.startDetailsActivity(activity, mov)
        }, MovieRecyclerViewAdapter.OnFavouriteClick { movie ->
            if (helperService.toDoWithFavourite(movie)) {
                Snackbar.make(view.list, "Added to favourite", Snackbar.LENGTH_SHORT)
                        .show()
            } else {
                buildAlertDialog(movie, view.list)
            }
            view.list.adapter.notifyDataSetChanged()
        }), object :MovieListWrapper.OnEndReachListener {
            override fun onEndButtonClick() {
            }

            override fun onEndReach(): MovieListWrapper.State {
                if (list.size > 0 && message == null) {
                    if (type == TYPE_MOVIE) {
                        movieRequest()
                        return MovieListWrapper.State.Loading
                    } else {
                        tvShowRequest()
                        return MovieListWrapper.State.Loading
                    }
                }
                return MovieListWrapper.State.end
            }
            /*v.mProgressBar.visibility = View.GONE
            if (list.size > 0 && message == null) {
                v.mProgressBar.visibility = View.VISIBLE
                if (type == TYPE_MOVIE) {
                    movieRequest()
                } else {
                    tvShowRequest()
                }
            }*/


        })
        return view
    }


    private fun movieRequest() {
        service.getMovieByKeyword(query, page, object : Callback<Page> {
            override fun onResponse(call: Call<Page>?, response: Response<Page>?) {
                if (response!!.body()!!.page == page) {
                    page++
                    list.addAll(Mapper.mapFromMovieToTVEntity(response.body()!!.movies))
                    if (response.body()!!.movies.isEmpty()) {
                        message = "Overload"
                    }
                    mRecyclerView!!.adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<Page>?, t: Throwable?) {
                Snackbar.make(mRecyclerView!!, "No Internet", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun tvShowRequest() {
        repository.getTVShowByKeyword(query, page, object : Callback<TVPage> {
            override fun onResponse(call: Call<TVPage>?, response: Response<TVPage>?) {
                if (response!!.body()!!.page == page) {
                    page++
                    list.addAll(Mapper.mapFromTVShowToTVEntity(response.body()!!.results))
                    mRecyclerView!!.adapter.notifyDataSetChanged()
                    if (response.body()!!.results.isEmpty()) {
                        message = "Overload"
                    }
                }
            }

            override fun onFailure(call: Call<TVPage>?, t: Throwable?) {
                Snackbar.make(mRecyclerView!!, "No Internet", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mRecyclerView!!.adapter.notifyDataSetChanged()
    }

    private fun buildAlertDialog(movie: TVEntity, view: RecyclerView) {
        AlertDialog.Builder(activity)
                .setMessage(R.string.delete_from_watched)
                .setPositiveButton(R.string.yes) { _, _ ->
                    dbService.deleteFromDb(movie.id)
                    Snackbar.make(view, "Deleted from favourite",
                            Snackbar.LENGTH_LONG).show()
                    view.adapter.notifyDataSetChanged()
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    dbService.cancelFavourite(movie.id)
                    Snackbar.make(view, "Deleted from favourite",
                            Snackbar.LENGTH_LONG).show()
                    view.adapter.notifyDataSetChanged()
                }.create()
                .show()
    }
}
