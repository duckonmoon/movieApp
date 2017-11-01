package movies.test.softserve.movies.adapter;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RatingBar;

import com.squareup.picasso.Picasso;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.activity.MovieDetailsActivity;
import movies.test.softserve.movies.controllers.MainController;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.event.AddedItemsEvent;
import movies.test.softserve.movies.service.DBService;
import movies.test.softserve.movies.viewholder.MovieViewHolder;

/**
 * Created by rkrit on 20.10.17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private AppCompatActivity mActivity;
    private MainController mainController;

    public static final int VIEW_TYPE_CELL = 0;
    public static final int VIEW_TYPE_FOOTER = 1;

    public MovieListAdapter(AppCompatActivity activity) {
        mActivity = activity;
        mainController = MainController.getInstance();
        mainController.setAddedItemsEventListener(new AddedItemsEvent() {
            @Override
            public void onItemsAdded() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        });
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
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {
        if (holder.mImageView != null) {
            holder.mTextView.setText("" + (1 + position) + ". " + mainController.getMovies().get(position).getTitle() + "\n" + mainController.getMovies().get(position).getVoteAverage()
                    + "\n" + mainController.getMovies().get(position).getVoteCount());
            holder.mRatingBar.setRating(mainController.getMovies().get(position).getVoteAverage().floatValue() / 2);
            holder.mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                }
            });
            holder.mFavourite.setImageResource(DBService.getInstance().checkIfMovieIsFavourite(mainController.getMovies().get(position).getId())?R.drawable.ic_stary_black_24dp:R.drawable.ic_star_border_black_24dp);
            holder.mFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movie movie = mainController.getMovies().get(position);
                    DBService dbService = DBService.getInstance();
                    if(dbService.checkIfMovieIsFavourite(movie.getId())){
                        holder.mFavourite.setImageResource(R.drawable.ic_star_border_black_24dp);
                        dbService.deleteMovieFromDb(movie.getId());
                        Snackbar.make(mActivity.findViewById(R.id.recyclerview), "Deleted from favourite", Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        holder.mFavourite.setImageResource(R.drawable.ic_stary_black_24dp);
                        dbService.insertMovie(movie.getId(),
                                movie.getTitle(),
                                movie.getVoteAverage().floatValue(),
                                movie.getVoteCount(),
                                movie.getOverview(),
                                movie.getReleaseDate(),
                                movie.getPosterPath());
                        Snackbar.make(mActivity.findViewById(R.id.recyclerview), "Added to favourite", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
            Picasso
                    .with(holder.mImageView.getContext())
                    .load("https://image.tmdb.org/t/p/w500" + mainController.getMovies().get(position)
                            .getPosterPath())
                    .into(holder.mImageView);
            holder.mViewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, MovieDetailsActivity.class);
                    intent.putExtra(MovieDetailsActivity.ID, mainController.getMovies().get(position).getId());
                    intent.putExtra(MovieDetailsActivity.TITLE, mainController.getMovies().get(position).getTitle());
                    intent.putExtra(MovieDetailsActivity.POSTER_PATH, mainController.getMovies().get(position).getPosterPath());
                    intent.putExtra(MovieDetailsActivity.RELEASE_DATE, mainController.getMovies().get(position).getReleaseDate());
                    intent.putExtra(MovieDetailsActivity.VOTE_COUNT, mainController.getMovies().get(position).getVoteCount());
                    intent.putExtra(MovieDetailsActivity.VOTE_AVERAGE, mainController.getMovies().get(position).getVoteAverage());
                    intent.putExtra(MovieDetailsActivity.OVERVIEW, mainController.getMovies().get(position).getOverview());
                    mActivity.startActivity(intent);

                }
            });
        } else {
            if (mainController.getErrorMessage() == null) {
                mainController.requestMore();
            } else {
                holder.mProgressBar.setVisibility(View.GONE);
                holder.mButton.setVisibility(View.VISIBLE);
                holder.mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainController.requestMore();
                        holder.mButton.setVisibility(View.GONE);
                        holder.mProgressBar.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mainController.getMovies().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mainController.getMovies().size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }


}
