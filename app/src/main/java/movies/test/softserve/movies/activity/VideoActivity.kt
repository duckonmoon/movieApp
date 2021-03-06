package movies.test.softserve.movies.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_video.*
import movies.test.softserve.movies.R
import movies.test.softserve.movies.adapter.VideoAdapter
import movies.test.softserve.movies.controller.MainController
import movies.test.softserve.movies.entity.Video
import movies.test.softserve.movies.event.OnVideoGetListener
import movies.test.softserve.movies.fragment.YoutubeFragment
import movies.test.softserve.movies.service.MovieService
import java.util.*


class VideoActivity : BaseActivity() {

    companion object {
        val MOVIE_ID = "MOVIE_ID"
    }

    private var service = MovieService.getInstance()
    private var controller = MainController.getInstance()

    private lateinit var frame: FrameLayout
    private lateinit var recycler: RecyclerView

    private var youtubeFragment: YoutubeFragment? = null

    private var list: ArrayList<Video> = ArrayList()

    private var movieId = 0

    private var firstTry = true

    private var onVideoGetListener = object : OnVideoGetListener {
        override fun onVideoGet(videos: List<Video>) {
            if (list.isEmpty() && videos.isNotEmpty()) {
                list.addAll(videos)
                recycler.adapter.notifyDataSetChanged()
                empty.visibility = View.GONE
            }
            if (videos.isEmpty() && Locale.getDefault().language == "uk" && firstTry) {
                service.tryToGetVideos(movieId, null)
                firstTry = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
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
                if (controller.isAppInstalled("com.google.android.youtube")) {
                    youtubeFragment = YoutubeFragment.newInstance(video.key)
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame, youtubeFragment)
                    transaction.commit()
                } else {
                    val webIntent = Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + video.key))
                    startActivity(webIntent)
                }
            }
        })


    }

    override fun onResume() {
        super.onResume()
        service.addOnVideoGetListener(onVideoGetListener)
        if (list.size < 1) {
            service.tryToGetVideos(movieId, Locale.getDefault().language)
        }
    }

    override fun onPause() {
        super.onPause()
        service.removeOnVideoGetListener(onVideoGetListener)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame)
        if (fragment is YoutubeFragment) {
            fragment.onBackPressed()
            supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
            return
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
