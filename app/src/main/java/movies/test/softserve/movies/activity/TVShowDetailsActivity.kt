package movies.test.softserve.movies.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.content_movie_details.*
import movies.test.softserve.movies.R
import movies.test.softserve.movies.entity.FullTVShow
import movies.test.softserve.movies.entity.TVShow
import movies.test.softserve.movies.event.OnFullTVShowGetListener
import movies.test.softserve.movies.event.OnInfoUpdatedListener
import movies.test.softserve.movies.repository.TVShowsRepository
import movies.test.softserve.movies.service.DBMovieService
import movies.test.softserve.movies.viewmodel.FullTVSeriesViewModel

class TVShowDetailsActivity : AppCompatActivity() {

    companion object {
        val ID = "id"
        val NAME = "name"
        val VOTE_AVERAGE = "vote average"
        val VOTE_COUNT = "vote count"
        val POSTER_PATH = "poster path"
        val OVERVIEW = "overview"
    }

    private var viewModel: FullTVSeriesViewModel = FullTVSeriesViewModel()

    private var mCurrentAnimator: Animator? = null
    private var mBitmapDrawable: BitmapDrawable? = null

    private var mShortAnimationDuration: Int = 0

    private var listener : OnFullTVShowGetListener? = null
    private var onInfoUpdatedListener : OnInfoUpdatedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FullTVSeriesViewModel::class.java)

        mShortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        initView()
        getIntentInfo()
        useIntentInfo()
        if (savedInstanceState!=null){
            if (viewModel.fullTVShow!=null)
                getFullInfo(viewModel.fullTVShow!!)
        }
    }

    override fun onResume() {
        super.onResume()
        if (listener==null){
            if (viewModel.fullTVShow==null) {
                listener = object : OnFullTVShowGetListener {
                    override fun onFullTVShowGet(tvShow: FullTVShow) {
                        viewModel.fullTVShow = tvShow
                        getFullInfo(tvShow)
                    }
                }
                TVShowsRepository.getInstance().addOnFullTVShowGetListeners(listener)
                TVShowsRepository.getInstance().trytoGetFullTVShow(viewModel.tvShow!!.id)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (listener!=null){
            TVShowsRepository.getInstance().removeOnFullTVShowGetListeners(listener)
            listener = null
        }
        if (onInfoUpdatedListener!=null){
            TVShowsRepository.getInstance().removeOnInfoUpdatedListener(onInfoUpdatedListener!!)
            listener = null
        }
    }
    private fun initView() {
        setContentView(R.layout.activity_movie_details)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            val dbService = DBMovieService.getInstance()
            if (!dbService.checkIfIsFavourite(viewModel.tvShow!!.id)) {
                if (!dbService.checkIfExists(viewModel.tvShow!!.id)) {
                    dbService.insertTVShowToFavourite(viewModel.tvShow!!.id,
                            viewModel.tvShow!!.title,
                            viewModel.tvShow!!.voteAverage!!.toFloat(),
                            viewModel.tvShow!!.voteCount!!,
                            viewModel.tvShow!!.overview,
                            viewModel.tvShow!!.posterPath)
                } else {
                    dbService.setFavourite(viewModel.tvShow!!.id)
                }
                fab.setImageResource(R.drawable.ic_stars_black_24dp)
                Snackbar.make(view, "Added to favourite", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                watched.setImageResource(R.mipmap.checked)
            } else {
                dbService.cancelFavourite(viewModel.tvShow!!.id)
                fab.setImageResource(R.drawable.ic_star_border_black_24dp)
                Snackbar.make(view, "Removed to favourite", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        }
    }

    private fun getIntentInfo() {
        if (intent != null && viewModel.tvShow == null) {
            val tvShow = TVShow()
            tvShow.id = intent.extras[ID] as Int
            tvShow.name = intent.extras[NAME] as String
            tvShow.voteAverage = intent.extras[VOTE_AVERAGE] as Double
            tvShow.voteCount = intent.extras[VOTE_COUNT] as Int
            tvShow.posterPath = intent.extras[POSTER_PATH] as String
            tvShow.overview = intent.extras[OVERVIEW] as String
            viewModel.tvShow = tvShow

        }
        watched.setImageResource(if (DBMovieService.getInstance().checkIfExists(viewModel.tvShow!!.id)) R.mipmap.checked else R.mipmap.not_checked)
        watched.setOnClickListener {
            if (!DBMovieService.getInstance().checkIfExists(viewModel.tvShow!!.id)) {
                watched.setImageResource(R.mipmap.checked)
                DBMovieService.getInstance().addTVShowToDb(viewModel.tvShow!!.id,
                        viewModel.tvShow!!.title,
                        viewModel.tvShow!!.voteAverage!!.toFloat(),
                        viewModel.tvShow!!.voteCount!!,
                        viewModel.tvShow!!.overview,
                        viewModel.tvShow!!.posterPath
                )
                Snackbar.make(findViewById(R.id.nested_scroll_view), "Added to watched", Snackbar.LENGTH_SHORT).show()
            } else {

                if (DBMovieService.getInstance().checkIfIsFavourite(viewModel.tvShow!!.id)) {
                    Snackbar.make(findViewById(R.id.nested_scroll_view), "It's favourite, u cant do this", Snackbar.LENGTH_SHORT).show()
                } else {
                    val builder = AlertDialog.Builder(this@TVShowDetailsActivity)
                    builder.setMessage(R.string.confirm)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                DBMovieService.getInstance().deleteFromDb(viewModel.tvShow!!.id)
                                watched.setImageResource(R.mipmap.not_checked)
                                Snackbar.make(findViewById(R.id.nested_scroll_view), "Marked as unwatched", Snackbar.LENGTH_SHORT).show()
                            }
                            .setNegativeButton(R.string.no) { _, _ -> }
                    builder.create().show()
                }
            }
        }
        if (DBMovieService.getInstance().checkIfIsFavourite(viewModel.tvShow!!.id)) {
            fab.setImageResource(R.drawable.ic_stars_black_24dp)
        }
    }

    private fun useIntentInfo() {
        toolbar_layout.title = viewModel.tvShow!!.name
        toolbar_layout.setOnClickListener({ zoomImageFromThumb(toolbar_layout, mBitmapDrawable!!) })
        ratingBar.rating = viewModel.tvShow!!.voteAverage!!.toFloat() / 2
        ratingBar.setOnRatingBarChangeListener({ _, rating, fromUser ->  if (fromUser) {
            onInfoUpdatedListener = OnInfoUpdatedListener { code ->
                ratingBar.rating = code
                Snackbar.make(findViewById(R.id.nested_scroll_view), "Your rating saved", Snackbar.LENGTH_LONG).show()
            }
            TVShowsRepository.getInstance().addOnInfoUpdatedListener(onInfoUpdatedListener!!)
            TVShowsRepository.getInstance().rateTVShow(viewModel.tvShow!!.id,rating*2)
        }})
        vote_count.text = "" + Math.round(viewModel.tvShow!!.voteAverage!! * 10).toFloat() / 10 + "/" + viewModel.tvShow!!.voteCount
        overview.text = viewModel.tvShow!!.overview
        release_date.visibility = View.GONE
        share.setOnClickListener{
            val shareLinkContent = ShareLinkContent.Builder()
                    .setQuote(viewModel.tvShow!!.name + "     \r\nPlot: " + viewModel.tvShow!!.overview)
                    .setContentUrl(Uri.parse("https://image.tmdb.org/t/p/w500" + viewModel.tvShow!!.posterPath))
                    .build()
            ShareDialog.show(this@TVShowDetailsActivity, shareLinkContent)}
        Picasso
                .with(this)
                .load("https://image.tmdb.org/t/p/w500" + viewModel.tvShow!!.posterPath)
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                        mBitmapDrawable = BitmapDrawable(resources, bitmap)
                        mBitmapDrawable!!.gravity = Gravity.NO_GRAVITY
                        toolbar_layout.background = mBitmapDrawable
                    }

                    override fun onBitmapFailed(errorDrawable: Drawable) {
                        Log.d("TAG", "FAILED")
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                        Log.d("TAG", "Prepare Load")
                    }
                })
    }

    private fun getFullInfo(tvShow: FullTVShow){
        runOnUiThread{
            release_date.visibility = View.VISIBLE
            release_date.text = "" + release_date.text + tvShow.firstAirDate
            if (tvShow.homepage != null && tvShow.homepage != ""){
                links.text = tvShow.homepage
                links.setOnClickListener({
                    val webPage = Uri.parse(tvShow.homepage)
                    val webIntent = Intent(Intent.ACTION_VIEW, webPage)
                    startActivity(webIntent)})
            }
            for (i in tvShow.seasons!!.indices){
                var image = ImageView(this@TVShowDetailsActivity)
                Picasso
                        .with(this@TVShowDetailsActivity)
                        .load("https://image.tmdb.org/t/p/w500" + viewModel.fullTVShow!!.seasons!![i].posterPath)
                        .into(image)
                image.setPadding(0,20,20,0)
                genres.addView(image)
            }
        }
    }

    private fun zoomImageFromThumb(thumbView: View, bitmapDrawable: BitmapDrawable) {
        if (mCurrentAnimator != null) {
            mCurrentAnimator!!.cancel()
        }

        val expandedImageView = findViewById<ImageView>(R.id.expanded_image)
        expandedImageView.setImageDrawable(bitmapDrawable)

        val startBounds = Rect()
        val finalBounds = Rect()
        val globalOffset = Point()

        thumbView.getGlobalVisibleRect(startBounds)
        findViewById<View>(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset)
        startBounds.offset(-globalOffset.x, -globalOffset.y)
        finalBounds.offset(-globalOffset.x, -globalOffset.y)

        val startScale: Float
        if (finalBounds.width().toFloat() / finalBounds.height() > startBounds.width().toFloat() / startBounds.height()) {
            startScale = startBounds.height().toFloat() / finalBounds.height()
            val startWidth = startScale * finalBounds.width()
            val deltaWidth = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            startScale = startBounds.width().toFloat() / finalBounds.width()
            val startHeight = startScale * finalBounds.height()
            val deltaHeight = (startHeight - startBounds.height()) / 2
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        thumbView.alpha = 0f
        expandedImageView.visibility = View.VISIBLE

        expandedImageView.pivotX = 0f
        expandedImageView.pivotY = 0f

        var set = AnimatorSet()
        set
                .play(ObjectAnimator.ofFloat<View>(expandedImageView, View.X, startBounds.left.toFloat(),
                        finalBounds.left.toFloat()))
                .with(ObjectAnimator.ofFloat<View>(expandedImageView, View.Y, startBounds.top.toFloat(),
                        finalBounds.top.toFloat()))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
        set.duration = mShortAnimationDuration.toLong()
        set.interpolator = DecelerateInterpolator()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mCurrentAnimator = null
            }

            override fun onAnimationCancel(animation: Animator) {
                mCurrentAnimator = null
            }
        })
        set.start()
        mCurrentAnimator = set

        expandedImageView.setOnClickListener {
            if (mCurrentAnimator != null) {
                mCurrentAnimator!!.cancel()
            }

            set = AnimatorSet()
            set
                    .play(ObjectAnimator.ofFloat<View>(expandedImageView, View.X, startBounds.left.toFloat()))
                    .with(ObjectAnimator.ofFloat<View>(expandedImageView, View.Y, startBounds.top.toFloat()))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView, View.SCALE_X, startScale))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView, View.SCALE_Y, startScale))
            set.duration = mShortAnimationDuration.toLong()
            set.interpolator = DecelerateInterpolator()
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    thumbView.alpha = 1f
                    expandedImageView.visibility = View.GONE
                    mCurrentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    thumbView.alpha = 1f
                    expandedImageView.visibility = View.GONE
                    mCurrentAnimator = null
                }
            })
            set.start()
            mCurrentAnimator = set
        }
    }
}
