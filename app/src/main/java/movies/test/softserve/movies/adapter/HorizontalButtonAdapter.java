package movies.test.softserve.movies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.entity.AdditionalInfo;

/**
 * Created by rkrit on 27.11.17.
 */

public class HorizontalButtonAdapter extends RecyclerView.Adapter<HorizontalButtonAdapter.ViewHolder> {

    private List<? extends AdditionalInfo> items;
    private OnClickListener listener;

    public HorizontalButtonAdapter(List<? extends AdditionalInfo> items, OnClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_button_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mButton.setText(items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface OnClickListener {
        void onClick(AdditionalInfo info);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button mButton;

        public ViewHolder(View view) {
            super(view);
            mButton = itemView.findViewById(R.id.item_button);
            mButton.setOnClickListener((v) -> {
                listener.onClick(items.get(getAdapterPosition()));
            });
        }
    }
}
