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
//TODO change wrapper on end reach dont let fragment know about holder
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
            if (getItemViewType(position) == VIEW_TYPE_FOOTER) {
                final ViewHolder viewHolder = (ViewHolder) holder;
                switch (mOnEndReachListener.onEndReach()) {
                    case Loading:
                        viewHolder.mProgressBar.setVisibility(View.VISIBLE);
                        viewHolder.mButton.setVisibility(View.GONE);
                        break;
                    case Failed:
                        viewHolder.bind();
                        viewHolder.mProgressBar.setVisibility(View.GONE);
                        viewHolder.mButton.setVisibility(View.VISIBLE);
                        break;
                    case end:
                        viewHolder.mProgressBar.setVisibility(View.GONE);
                        viewHolder.mButton.setVisibility(View.GONE);
                        break;
                }
            }
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
            super(view, new Delegate() {
                @Override
                public void onMovieSelect(int position) {

                }

                @Override
                public void onFavouriteClick(int position) {

                }
            });
            mProgressBar = itemView.findViewById(R.id.spinner);
            mButton = itemView.findViewById(R.id.error_button);
        }

        @Override
        public void bind() {
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mButton.setVisibility(View.GONE);
                    mOnEndReachListener.onEndButtonClick();
                }
            });
        }
    }

    public interface OnEndReachListener {
        State onEndReach();
        void onEndButtonClick();
    }

    public enum State{
        Loading,
        Failed,
        end
    }
}
