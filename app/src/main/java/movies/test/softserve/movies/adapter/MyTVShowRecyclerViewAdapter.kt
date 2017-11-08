package movies.test.softserve.movies.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import movies.test.softserve.movies.R

import movies.test.softserve.movies.entity.TVShow
import movies.test.softserve.movies.viewholder.MainViewHolder

class MyTVShowRecyclerViewAdapter(private val tvShows: List<TVShow>, private val onMovieSelect: OnMovieSelect, private val onFavouriteClick: OnFavouriteClick) : RecyclerView.Adapter<MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_layout, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.mTextView.text = ("" + (1 + position) + ". " + tvShows[position].name
                + "\n" + Math.round(tvShows[position].voteAverage!! * 10).toFloat() / 10
                + "\n" + tvShows[position].voteCount)
        holder.mRatingBar.rating = tvShows[position].voteAverage!!.toFloat() / 2
        holder.mViewGroup.setOnClickListener { onMovieSelect.OnMovieSelected(tvShows[position]) }
        holder.mFavourite.setOnClickListener { onFavouriteClick.onFavouriteClick(tvShows[position])}
        Picasso
                .with(holder.mImageView.context)
                .load("https://image.tmdb.org/t/p/w500" + tvShows[position].posterPath)
                .into(holder.mImageView)
    }

    override fun getItemCount(): Int {
        return tvShows.size
    }

    interface OnMovieSelect {
        fun OnMovieSelected(tvShow: TVShow)
    }

    interface OnFavouriteClick {
        fun onFavouriteClick(tvShow: TVShow)
    }
}
