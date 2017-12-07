package movies.test.softserve.movies.activity

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.FrameLayout
import movies.test.softserve.movies.R
import movies.test.softserve.movies.adapter.VideoAdapter
import movies.test.softserve.movies.entity.Video
import movies.test.softserve.movies.event.OnVideoGetListener
import movies.test.softserve.movies.fragment.YoutubeFragment
import movies.test.softserve.movies.service.MovieService

class VideoActivity : BaseActivity() {

    companion object {
        val MOVIE_ID = "MOVIE_ID"
    }

    private var service = MovieService.getInstance()

    private lateinit var frame: FrameLayout
    private lateinit var recycler: RecyclerView

    private var list: ArrayList<Video> = ArrayList()

    private var movieId = 0

    private var onVideoGetListener = object : OnVideoGetListener {
        override fun onVideoGet(videos: List<Video>) {
            if (list.size < 1) {
                list.addAll(videos)
                recycler.adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        frame = findViewById(R.id.frame)
        recycler = findViewById(R.id.recycler)

        movieId = intent.extras.getInt(MOVIE_ID, 0)

        val isTablet = resources.getBoolean(R.bool.isTablet)
        if (isTablet) {
            recycler.layoutManager = GridLayoutManager(this, 2)
        } else {
            recycler.layoutManager = LinearLayoutManager(this)
        }

        recycler.adapter = VideoAdapter(list, object : VideoAdapter.OnItemClickListener {
            override fun onItemClick(video: Video) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, YoutubeFragment.newInstance(video.key))
                transaction.commit()
            }
        })


    }

    override fun onResume() {
        super.onResume()
        service.addOnVideoGetListener(onVideoGetListener)
        if (list.size < 1) {
            service.tryToGetVideos(movieId)
        }
    }

    override fun onPause() {
        super.onPause()
        service.removeOnVideoGetListener(onVideoGetListener)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.size > 0) {
            val transaction = supportFragmentManager.beginTransaction()
            for (fragment in supportFragmentManager.fragments) {
                transaction.remove(fragment)
            }
            transaction.commit()
        } else {
            super.onBackPressed()
        }

    }
}
