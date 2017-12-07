package movies.test.softserve.movies.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import movies.test.softserve.movies.R

/**
 * Created by User on 07.12.2017.
 */
class VideoViewHolder : RecyclerView.ViewHolder {
    val descriptionText: TextView
    val videoImage: ImageView


    constructor(mView: View, onItemClickListener: OnItemClickListener) : super(mView) {
        descriptionText = mView.findViewById(R.id.description)
        videoImage = mView.findViewById(R.id.video_image)
        mView.setOnClickListener { onItemClickListener.onItemClick(adapterPosition) }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}