package movies.test.softserve.movies.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import movies.test.softserve.movies.R;

/**
 * Created by rkrit on 06.11.17.
 */


public class MainViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextView;
    public ImageView mImageView;
    public RatingBar mRatingBar;
    public ImageView mFavourite;
    public ViewGroup mViewGroup;
    public Delegate delegate;


    public MainViewHolder(View view, final Delegate delegate) {
        super(view);
        mTextView = view.findViewById(R.id.description);
        mImageView = view.findViewById(R.id.movie_image);
        mRatingBar = view.findViewById(R.id.rating);
        mFavourite = view.findViewById(R.id.lit_favourite);
        mViewGroup = view.findViewById(R.id.card_of_list);
        mFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.onFavouriteClick(getAdapterPosition());
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.onMovieSelect(getAdapterPosition());
            }
        });
        this.delegate = delegate;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public interface Delegate {
        void onMovieSelect(int position);

        void onFavouriteClick(int position);
    }
}
