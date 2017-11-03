package movies.test.softserve.movies.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import movies.test.softserve.movies.R;


/**
 * Created by rkrit on 20.10.17.
 */

public class MovieViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextView;
    public ImageView mImageView;
    public RatingBar mRatingBar;
    public ImageView mFavourite;
    public Button mButton;
    public ProgressBar mProgressBar;
    public ViewGroup mViewGroup;

    public MovieViewHolder(View itemView) {
        super(itemView);
        if (itemView.findViewById(R.id.description) != null) {
            mTextView = itemView.findViewById(R.id.description);
            mImageView = itemView.findViewById(R.id.movie_image);
            mRatingBar = itemView.findViewById(R.id.rating);
            mFavourite = itemView.findViewById(R.id.favourite);
            mViewGroup = itemView.findViewById(R.id.card_of_list);
        } else {
            mProgressBar = itemView.findViewById(R.id.spinner);
            mButton = itemView.findViewById(R.id.error_button);
        }
    }
}
