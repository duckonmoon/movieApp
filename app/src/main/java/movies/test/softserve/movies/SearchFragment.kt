package movies.test.softserve.movies

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import kotlinx.android.synthetic.main.fragment_tventity_list.view.*
import movies.test.softserve.movies.adapter.MyMovieListWrapper
import movies.test.softserve.movies.adapter.MyMovieRecyclerViewAdapter
import movies.test.softserve.movies.repository.TVShowsRepository
import movies.test.softserve.movies.service.MovieService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import movies.test.softserve.movies.activity.MovieDetailsActivity
import movies.test.softserve.movies.activity.TVShowDetailsActivity
import movies.test.softserve.movies.entity.*


class SearchFragment : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var searchView: ImageView? = null
    private var editSearchView: EditText? = null
    private var switchView: Switch? = null


    companion object {
        val TYPE_TV_SHOW = true
        val TYPE_MOVIE = false
        var query: String = ""
        var page: Int = 1
        var type: Boolean = false
        var list: ArrayList<TVEntity> = ArrayList()
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_tventity_list, container, false)

        if (view.list is RecyclerView) {
            val context = view.context
            view.container.setOnClickListener { }
            mRecyclerView = view.list
            searchView = view.search
            editSearchView = view.edit_query
            switchView = view.switcher

            searchView!!.setOnClickListener {
                if (editSearchView!!.text.toString() == "") {
                    return@setOnClickListener
                }
                val currView = activity.currentFocus
                if (currView != null) {
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view!!.windowToken, 0)
                }
                page = 1
                list.clear()
                query = editSearchView!!.text.toString()
                type = switchView!!.isChecked
                if (type == TYPE_MOVIE) {
                    movieRequest()
                } else {
                    tvShowRequest()
                }
            }

            mRecyclerView!!.list.layoutManager = LinearLayoutManager(context)
            mRecyclerView!!.list.adapter = MyMovieListWrapper(MyMovieRecyclerViewAdapter(list, MyMovieRecyclerViewAdapter.OnMovieSelect { mov ->
                if (mov is Movie) {
                    val intent = Intent(activity, MovieDetailsActivity::class.java)
                    intent.putExtra(MovieDetailsActivity.ID, mov.id)
                    intent.putExtra(MovieDetailsActivity.TITLE, mov.title)
                    intent.putExtra(MovieDetailsActivity.POSTER_PATH, mov.posterPath)
                    intent.putExtra(MovieDetailsActivity.RELEASE_DATE, mov.releaseDate)
                    intent.putExtra(MovieDetailsActivity.VOTE_COUNT, mov.voteCount)
                    intent.putExtra(MovieDetailsActivity.VOTE_AVERAGE, mov.voteAverage)
                    intent.putExtra(MovieDetailsActivity.OVERVIEW, mov.overview)
                    activity.startActivity(intent)
                } else if (mov is TVShow) {
                    val intent = Intent(activity, TVShowDetailsActivity::class.java)
                    intent.putExtra(TVShowDetailsActivity.ID, mov.id)
                    intent.putExtra(TVShowDetailsActivity.NAME, mov.title)
                    intent.putExtra(TVShowDetailsActivity.POSTER_PATH, mov.posterPath)
                    intent.putExtra(TVShowDetailsActivity.VOTE_COUNT, mov.voteCount)
                    intent.putExtra(TVShowDetailsActivity.VOTE_AVERAGE, mov.voteAverage)
                    intent.putExtra(TVShowDetailsActivity.OVERVIEW, mov.overview)
                    activity.startActivity(intent)
                }
            }, MyMovieRecyclerViewAdapter.OnFavouriteClick { }), { v ->
                if (v is MyMovieListWrapper.ViewHolder) {
                    v.mProgressBar.visibility = View.GONE
                    if (list.size > 0) {
                        v.mProgressBar.visibility = View.VISIBLE
                        if (type == TYPE_MOVIE) {
                            movieRequest()
                        } else {
                            tvShowRequest()
                        }
                    }
                }
            })
        }
        return view
    }


    private fun movieRequest() {
        MovieService.getInstance().getMovieByKeyword(query, page, object : Callback<Page> {
            override fun onResponse(call: Call<Page>?, response: Response<Page>?) {
                if (response!!.body()!!.page == page) {
                    page++
                    list.addAll(response!!.body()!!.movies)
                    mRecyclerView!!.adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<Page>?, t: Throwable?) {
                Snackbar.make(mRecyclerView!!, "No Internet", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun tvShowRequest() {
        TVShowsRepository.getInstance().getTVShowByKeyword(query, page, object : Callback<TVPage> {
            override fun onResponse(call: Call<TVPage>?, response: Response<TVPage>?) {
                if (response!!.body()!!.page == page) {
                    page++
                    list.addAll(response.body()!!.results)
                    mRecyclerView!!.adapter.notifyDataSetChanged()
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
}
