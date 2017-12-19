package movies.test.softserve.movies.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import movies.test.softserve.movies.R
import movies.test.softserve.movies.entity.Video
import movies.test.softserve.movies.viewholder.VideoViewHolder

class VideoAdapter(private var videos: List<Video>
                   , private val onItemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<VideoViewHolder>(), VideoViewHolder.OnItemClickListener {

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.descriptionText.text = video.name
        try {
            Picasso
                    .with(holder.videoImage.context)
                    .load("https://img.youtube.com/vi/" + video.key + "/0.jpg")
                    .into(holder.videoImage)
        } catch (e: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.video_list_item_layout, parent, false)
        return VideoViewHolder(view, this)
    }

    override fun onItemClick(position: Int) {
        onItemClickListener.onItemClick(videos[position])
    }

    @FunctionalInterface
    interface OnItemClickListener {
        fun onItemClick(video: Video)
    }
}