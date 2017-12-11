package movies.test.softserve.movies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.entity.ForImage;
import movies.test.softserve.movies.entity.Season;

/**
 * Created by rkrit on 27.11.17.
 */

public class HorizontalImageAdapter extends RecyclerView.Adapter<HorizontalImageAdapter.ViewHolder> {

    private List<? extends ForImage> items;
    private HorizontalImageAdapter.OnClickListener listener;

    public HorizontalImageAdapter(List<? extends ForImage> items, HorizontalImageAdapter.OnClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public HorizontalImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HorizontalImageAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_image_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(HorizontalImageAdapter.ViewHolder holder, int position) {
        Picasso.with(holder.mImage.getContext()).cancelRequest(holder.mImage);
        Picasso
                .with(holder.mImage.getContext())
                .load("https://image.tmdb.org/t/p/w500" + items.get(position).getPosterPath())
                .into(holder.mImage);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface OnClickListener {
        void onClick(ImageView imageView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImage;

        public ViewHolder(View view) {
            super(view);
            mImage = itemView.findViewById(R.id.item_image);
            mImage.setOnClickListener((v) -> listener.onClick(mImage));
        }
    }
}
