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


//TODO add listeners
public class MainViewHolder extends RecyclerView.ViewHolder{
    public TextView mTextView;
    public ImageView mImageView;
    public RatingBar mRatingBar;
    public ImageView mFavourite;
    public ViewGroup mViewGroup;
    public Delegate delegate;


    public interface Delegate{
        void onMovieSelect(int position);
        void onFavouriteClick(int position);
    }

    public MainViewHolder(View view, final Delegate delegate) {
        super(view);
        mTextView = itemView.findViewById(R.id.description);
        mImageView = itemView.findViewById(R.id.movie_image);
        mRatingBar = itemView.findViewById(R.id.rating);
        mFavourite = itemView.findViewById(R.id.favourite);
        mViewGroup = itemView.findViewById(R.id.card_of_list);
        this.delegate = delegate;
    }

    public void bind(){
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
    }




    @Override
    public String toString() {
        return super.toString();
    }
}
