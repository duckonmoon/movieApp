package movies.test.softserve.movies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.viewholder.MainViewHolder;


/**
 * Created by rkrit on 06.11.17.
 */

public class MovieListWrapper extends RecyclerView.Adapter<MainViewHolder> {

    private RecyclerView.Adapter<MainViewHolder> adapter;
    private OnEndReachListener mOnEndReachListener;
    private int VIEW_TYPE_CELL = 1;
    private int VIEW_TYPE_FOOTER = 0;

    public MovieListWrapper(RecyclerView.Adapter<MainViewHolder> adapter, @NonNull OnEndReachListener onEndReachListener) {
        this.adapter = adapter;
        mOnEndReachListener = onEndReachListener;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            return adapter.onCreateViewHolder(parent, viewType);
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        if (position < adapter.getItemCount()) {
            adapter.onBindViewHolder(holder, position);
        } else {
            mOnEndReachListener.onEndReach(holder);
        }
    }

    @Override
    public int getItemCount() {
        return adapter.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == adapter.getItemCount()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }


    public class ViewHolder extends MainViewHolder {
        public Button mButton;
        public ProgressBar mProgressBar;

        public ViewHolder(View view) {
            super(view);
            mProgressBar = itemView.findViewById(R.id.spinner);
            mButton = itemView.findViewById(R.id.error_button);
        }
    }

    public interface OnEndReachListener {
        void onEndReach(MainViewHolder mainViewHolder);
    }
}
