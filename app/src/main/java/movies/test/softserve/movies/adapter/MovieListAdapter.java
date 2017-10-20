package movies.test.softserve.movies.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.entity.Page;
import movies.test.softserve.movies.entity.Result;
import movies.test.softserve.movies.viewholder.MovieViewHolder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rkrit on 20.10.17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private List<Result> mPageList;


    public MovieListAdapter(List<Result> pageList) {
        mPageList = pageList;
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        holder.mTextView.setText(mPageList.get(position).getTitle() + "\n" + mPageList.get(position).getVoteAverage()
                + "\n" + mPageList.get(position).getVoteCount());
        Picasso
                .with(holder.mImageView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + mPageList.get(position)
                        .getPosterPath())
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mPageList.size();
    }

}
