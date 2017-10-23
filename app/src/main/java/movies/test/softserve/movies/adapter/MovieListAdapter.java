package movies.test.softserve.movies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.repository.MoviesRepository;
import movies.test.softserve.movies.viewholder.MovieViewHolder;

/**
 * Created by rkrit on 20.10.17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> implements Observer {
    private List<Movie> mPageList;
    private MoviesRepository moviesRepository;

    public static final int VIEW_TYPE_CELL = 0;
    public static final int VIEW_TYPE_FOOTER = 1;

    public MovieListAdapter() {
        mPageList = new ArrayList<>();
        moviesRepository = MoviesRepository.getInstance();
        moviesRepository.addObserver(this);
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false));
        }
        else {
            return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item_layout, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        if (holder.mImageView!=null) {
            holder.mTextView.setText(mPageList.get(position).getTitle() + "\n" + mPageList.get(position).getVoteAverage()
                    + "\n" + mPageList.get(position).getVoteCount());
            Picasso
                    .with(holder.mImageView.getContext())
                    .load("https://image.tmdb.org/t/p/w500" + mPageList.get(position)
                            .getPosterPath())
                    .into(holder.mImageView);
        }
        else
        {
            moviesRepository.trytogetAllMovies();
        }
    }

    @Override
    public int getItemCount() {
        return mPageList.size()+1;
    }


    @Override
    public int getItemViewType(int position) {
        return (position == mPageList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }


    @Override
    public void update(Observable o, Object arg) {
        mPageList.addAll(((MoviesRepository) o).getMovieList());
        notifyDataSetChanged();
    }
}
