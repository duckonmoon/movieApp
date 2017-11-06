package movies.test.softserve.movies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.service.DBService;
import movies.test.softserve.movies.viewholder.MainViewHolder;

public class MyMovieRecyclerViewAdapter extends RecyclerView.Adapter<MainViewHolder> {

    private List<Movie> movies;
    private OnMovieSelect event;
    private OnFavouriteClick favouriteClick;


    public MyMovieRecyclerViewAdapter(List<Movie> items, @NonNull OnMovieSelect onMovieSelect, @NonNull OnFavouriteClick onFavouriteClick) {
        movies = items;
        event = onMovieSelect;
        favouriteClick = onFavouriteClick;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        holder.mTextView.setText("" + (1 + position) + ". " + movies.get(position).getTitle() + "\n" + ((float) Math.round(movies.get(position).getVoteAverage() * 10)) / 10
                + "\n" + movies.get(position).getVoteCount());
        holder.mRatingBar.setRating(movies.get(position).getVoteAverage().floatValue() / 2);
        holder.mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            }
        });
        holder.mViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.OnMovieSelected(movies.get(position));
            }
        });
        holder.mFavourite.setImageResource(DBService.getInstance().checkIfMovieIsFavourite(movies.get(position).getId()) ? R.drawable.ic_stary_black_24dp : R.drawable.ic_star_border_black_24dp);
        holder.mFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favouriteClick.onFavouriteClick(movies.get(position));
            }
        });
        Picasso
                .with(holder.mImageView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + movies.get(position)
                        .getPosterPath())
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }



    public interface OnMovieSelect {
        void OnMovieSelected(Movie movie);
    }

    public interface OnFavouriteClick {
        void onFavouriteClick(Movie movie);
    }
}
