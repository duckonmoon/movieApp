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

public class MainViewHolder extends RecyclerView.ViewHolder{
    public TextView mTextView;
    public ImageView mImageView;
    public RatingBar mRatingBar;
    public ImageView mFavourite;
    public ViewGroup mViewGroup;

    public MainViewHolder(View view) {
        super(view);
        mTextView = itemView.findViewById(R.id.description);
        mImageView = itemView.findViewById(R.id.movie_image);
        mRatingBar = itemView.findViewById(R.id.rating);
        mFavourite = itemView.findViewById(R.id.favourite);
        mViewGroup = itemView.findViewById(R.id.card_of_list);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
