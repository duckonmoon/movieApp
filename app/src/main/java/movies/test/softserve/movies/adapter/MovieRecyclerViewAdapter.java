package movies.test.softserve.movies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.service.DBMovieService;
import movies.test.softserve.movies.viewholder.MainViewHolder;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MainViewHolder> implements MainViewHolder.Delegate {

    private List<TVEntity> movies;
    private OnMovieSelect event;
    private OnFavouriteClick favouriteClick;


    public MovieRecyclerViewAdapter(List<TVEntity> items, @NonNull OnMovieSelect onMovieSelect, @NonNull OnFavouriteClick onFavouriteClick) {
        movies = items;
        event = onMovieSelect;
        favouriteClick = onFavouriteClick;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false);
        return new MainViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        final TVEntity tvEntity = movies.get(position);
        Picasso.with(holder.mImageView.getContext()).cancelRequest(holder.mImageView);
        holder.mTextView.setText("" + (1 + position) + ". " + tvEntity.getTitle() + "\n" + ((float) Math.round(tvEntity.getVoteAverage() * 10)) / 10
                + "\n" + tvEntity.getVoteCount());
        holder.mRatingBar.setRating(tvEntity.getVoteAverage().floatValue() / 2);
        holder.mFavourite.setImageResource(DBMovieService.getInstance().checkIfIsFavourite(tvEntity.getId()) ? R.drawable.ic_stary_black_24dp : R.drawable.ic_star_border_black_24dp);
        Picasso
                .with(holder.mImageView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + tvEntity
                        .getPosterPath())
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<TVEntity> movies) {
        this.movies = movies;
    }

    @Override
    public void onMovieSelect(int position) {
        event.OnMovieSelected(movies.get(position));
    }

    @Override
    public void onFavouriteClick(int position) {
        favouriteClick.onFavouriteClick(movies.get(position));
    }


    public interface OnMovieSelect {
        void OnMovieSelected(TVEntity movie);
    }

    public interface OnFavouriteClick {
        void onFavouriteClick(TVEntity movie);
    }
}
