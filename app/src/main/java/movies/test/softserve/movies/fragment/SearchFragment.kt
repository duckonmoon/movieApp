package movies.test.softserve.movies.fragment

import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Switch
import kotlinx.android.synthetic.main.fragment_tventity_list.view.*
import movies.test.softserve.movies.R
import movies.test.softserve.movies.adapter.MovieListWrapper
import movies.test.softserve.movies.adapter.MovieRecyclerViewAdapter
import movies.test.softserve.movies.entity.Page
import movies.test.softserve.movies.entity.TVEntity
import movies.test.softserve.movies.entity.TVPage
import movies.test.softserve.movies.repository.TVShowsRepository
import movies.test.softserve.movies.service.DBHelperService
import movies.test.softserve.movies.service.DbMovieServiceRoom
import movies.test.softserve.movies.service.MovieService
import movies.test.softserve.movies.util.Mapper
import movies.test.softserve.movies.util.StartActivityClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


class SearchFragment : Fragment() {

    private var service: MovieService = MovieService.getInstance()
    private var repository: TVShowsRepository = TVShowsRepository.getInstance()
    private var dbService: DbMovieServiceRoom = DbMovieServiceRoom.getInstance()
    private var helperService: DBHelperService = DBHelperService()

    private val comp: String = "comp"
    private val typeMovie = false


    private lateinit var mRecyclerView: RecyclerView
    private var handler = Handler()

    private var transfer: Transfer = Transfer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            transfer = savedInstanceState.getSerializable(comp) as Transfer
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tventity_list, container, false)

        val context = view.context
        view.container.setOnClickListener { }
        mRecyclerView = view.list
        val editSearchView: EditText = view.edit_query
        val switchView: Switch = view.switcher

        view.search.setOnClickListener {
            if (editSearchView.text.toString() == "") {
                return@setOnClickListener
            }
            val currView = activity?.currentFocus
            if (currView != null) {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.windowToken, 0)
            }
            transfer.page = 1
            transfer.list.clear()
            transfer.query = editSearchView.text.toString()
            transfer.type = switchView.isChecked
            transfer.message = null
            if (transfer.type == typeMovie) {
                movieRequest()
            } else {
                tvShowRequest()
            }
        }

        val isTablet = resources.getBoolean(R.bool.isTablet)
        if (isTablet) {
            mRecyclerView.layoutManager = GridLayoutManager(context, 2)
        } else {
            mRecyclerView.layoutManager = LinearLayoutManager(context)

        }
        view.list.adapter = MovieListWrapper(MovieRecyclerViewAdapter(transfer.list, MovieRecyclerViewAdapter.OnMovieSelect { mov ->
            StartActivityClass.startDetailsActivity(activity, mov)
        }, MovieRecyclerViewAdapter.OnFavouriteClick { movie, position ->
            Thread {
                if (helperService.toDoWithFavourite(movie)) {
                    handler.post({
                        Snackbar.make(view.list, R.string.add_to_favourite, Snackbar.LENGTH_SHORT)
                                .show()
                    })
                } else {
                    handler.post({
                        buildAlertDialog(movie, position, view.list)
                    })
                }
                handler.post({
                    view.list.adapter.notifyItemChanged(position)
                })
            }.start()

        }), object : MovieListWrapper.OnEndReachListener {
            override fun onEndButtonClick() {
            }

            override fun onEndReach(): MovieListWrapper.State {
                if (transfer.list.size > 0 && transfer.message == null) {
                    if (transfer.type == typeMovie) {
                        movieRequest()
                    } else {
                        tvShowRequest()
                    }
                    return MovieListWrapper.State.Loading
                }
                return MovieListWrapper.State.end
            }
        })
        return view
    }


    private fun movieRequest() {
        service.getMovieByKeyword(transfer.query, transfer.page, object : Callback<Page> {
            override fun onResponse(call: Call<Page>?, response: Response<Page>?) {
                if (response!!.body()!!.page == transfer.page) {
                    transfer.page++
                    transfer.list.addAll(Mapper.mapFromMovieToTVEntity(response.body()!!.movies))
                    if (response.body()!!.movies.isEmpty()) {
                        transfer.message = "Overload"
                    }
                    mRecyclerView.adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<Page>?, t: Throwable?) {
                Snackbar.make(mRecyclerView, R.string.no_internet, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun tvShowRequest() {
        repository.getTVShowByKeyword(transfer.query, transfer.page, object : Callback<TVPage> {
            override fun onResponse(call: Call<TVPage>?, response: Response<TVPage>?) {
                if (response!!.body()!!.page == transfer.page) {
                    transfer.page++
                    transfer.list.addAll(Mapper.mapFromTVShowToTVEntity(response.body()!!.results))
                    mRecyclerView.adapter.notifyDataSetChanged()
                    if (response.body()!!.results.isEmpty()) {
                        transfer.message = "Overload"
                    }
                }
            }

            override fun onFailure(call: Call<TVPage>?, t: Throwable?) {
                Snackbar.make(mRecyclerView, R.string.no_internet, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mRecyclerView.adapter.notifyDataSetChanged()
    }

    private fun buildAlertDialog(movie: TVEntity, position: Int, view: RecyclerView) {
        AlertDialog.Builder(activity!!)
                .setMessage(R.string.delete_from_watched)
                .setPositiveButton(R.string.yes) { _, _ ->
                    Thread {
                        Runnable { dbService.deleteFromDb(movie) }
                    }.start()
                    Snackbar.make(view, R.string.mark_unwatched,
                            Snackbar.LENGTH_LONG).show()
                    view.adapter.notifyItemChanged(position)
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    Thread {
                        Runnable {
                            dbService.cancelFavourite(movie)
                        }
                    }.start()
                    Snackbar.make(view, R.string.removed_from_favourite,
                            Snackbar.LENGTH_LONG).show()
                    view.adapter.notifyItemChanged(position)
                }.create()
                .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(comp, transfer)
    }


}

private class Transfer : Serializable {
    var message: String? = null
    var query: String = ""
    var page: Int = 1
    var type: Boolean = false
    var list: ArrayList<TVEntity> = ArrayList()
}