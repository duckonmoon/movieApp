package movies.test.softserve.movies.adapter;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.fragment.MovieFragment;
import movies.test.softserve.movies.repository.MoviesRepository;
import movies.test.softserve.movies.viewholder.MovieViewHolder;

/**
 * Created by rkrit on 20.10.17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> implements Observer {
    private List<Movie> mPageList;
    private MoviesRepository moviesRepository;
    private String errorMessage;
    private AppCompatActivity mActivity;

    private static MovieListAdapter INSTANCE;

    public static final int VIEW_TYPE_CELL = 0;
    public static final int VIEW_TYPE_FOOTER = 1;

    private MovieListAdapter(AppCompatActivity activity) {
        mPageList = new ArrayList<>();
        moviesRepository = MoviesRepository.getInstance();
        moviesRepository.addObserver(this);
        mActivity = activity;
    }

    public static MovieListAdapter getInstance(AppCompatActivity activity) {
        if (INSTANCE==null) {
            INSTANCE = new MovieListAdapter(activity);
        }
        return INSTANCE;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false));
        } else {
            return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item_layout, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder,final int position) {
        if (holder.mImageView != null) {
            holder.mTextView.setText("" + (1+position)+ ". " + mPageList.get(position).getTitle() + "\n" + mPageList.get(position).getVoteAverage()
                    + "\n" + mPageList.get(position).getVoteCount());
            holder.mRatingBar.setRating(mPageList.get(position).getVoteAverage().floatValue()/2);
            holder.mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                }
            });
            Picasso
                    .with(holder.mImageView.getContext())
                    .load("https://image.tmdb.org/t/p/w500" + mPageList.get(position)
                            .getPosterPath())
                    .into(holder.mImageView);
            holder.mViewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MovieFragment.newInstance(mPageList.get(position)).show(mActivity.getSupportFragmentManager(), "movie_frag,e");
                }
            });
        } else {
            if (errorMessage==null) {
                moviesRepository.tryToGetAllMovies();
            }
            else {
                holder.mProgressBar.setVisibility(View.GONE);
                holder.mButton.setVisibility(View.VISIBLE);
                holder.mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moviesRepository.tryToGetAllMovies();
                        holder.mButton.setVisibility(View.GONE);
                        holder.mProgressBar.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPageList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mPageList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (((MoviesRepository) o).getMovieList()!=null) {
            mPageList.addAll(((MoviesRepository) o).getMovieList());
            errorMessage= null;
        }
        else {
            errorMessage = ((MoviesRepository) o).getMessage();
            Snackbar.make(mActivity.findViewById(R.id.constraint_layout),errorMessage,Snackbar.LENGTH_LONG).show();
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

    }
}
