package movies.test.softserve.movies.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import movies.test.softserve.movies.R;


/**
 * Created by rkrit on 20.10.17.
 */

public class MovieViewHolder extends RecyclerView.ViewHolder{
    public TextView mTextView;
    public ImageView mImageView;
    public MovieViewHolder(View itemView) {
        super(itemView);
        if (itemView.findViewById(R.id.description)!= null) {
            mTextView = itemView.findViewById(R.id.description);
            mImageView = itemView.findViewById(R.id.movie_image);
        }
    }
}
