package movies.test.softserve.movies.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.HorizontalImageAdapter;
import movies.test.softserve.movies.entity.FullTVShow;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.event.OnFullTVShowGetListener;
import movies.test.softserve.movies.event.OnInfoUpdatedListener;
import movies.test.softserve.movies.repository.TVShowsRepository;
import movies.test.softserve.movies.service.DBHelperService;
import movies.test.softserve.movies.service.DBMovieService;
import movies.test.softserve.movies.viewmodel.FullTVSeriesViewModel;

/**
 * Created by rkrit on 20.11.17.
 */

public class TVShowDetailsActivity extends BaseActivity {
    public static final String TV_ENTITY = "tv entity";

    private TVEntity tvShow;
    private FullTVSeriesViewModel viewModel;

    private DBHelperService helperService = new DBHelperService();
    private DBMovieService dbService = DBMovieService.getInstance();
    private TVShowsRepository repository = TVShowsRepository.getInstance();

    private Animator mCurrentAnimator;
    private BitmapDrawable mBitmapDrawable;
    private int mShortAnimationDuration;
    private OnFullTVShowGetListener listenerW;
    private OnInfoUpdatedListener onInfoUpdatedListener;

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ImageView watched;
    private CollapsingToolbarLayout toolbarLayout;
    private RatingBar ratingBar;
    private TextView voteCountView;
    private TextView overviewView;
    private TextView releaseDateView;
    private ImageView share;
    private TextView links;
    private RecyclerView seasons;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FullTVSeriesViewModel.class);

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        initView();
        getIntentInfo();
        useIntentInfo();
        if (savedInstanceState != null) {
            if (viewModel.getFullTVShow() != null)
                getFullInfo(viewModel.getFullTVShow());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listenerW == null) {
            if (viewModel.getFullTVShow() == null) {
                listenerW = new OnFullTVShowGetListener() {
                    @Override
                    public void onFullTVShowGet(FullTVShow tvShow) {
                        viewModel.setFullTVShow(tvShow);
                        getFullInfo(tvShow);
                    }
                };
                repository.addOnFullTVShowGetListeners(listenerW);
                repository.trytoGetFullTVShow(tvShow.getId());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (listenerW != null) {
            repository.removeOnFullTVShowGetListeners(listenerW);
            listenerW = null;
        }
        if (onInfoUpdatedListener != null) {
            repository.removeOnInfoUpdatedListener(onInfoUpdatedListener);
            onInfoUpdatedListener = null;
        }
    }

    private void getFullInfo(final FullTVShow fullTVShow) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                releaseDateView.setVisibility(View.VISIBLE);
                releaseDateView.setText(getString(R.string.release_date, fullTVShow.getFirstAirDate()));
                if (fullTVShow.getHomepage() != null && !fullTVShow.getHomepage().equals("")) {
                    links.setText(fullTVShow.getHomepage());
                    links.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri webPage = Uri.parse(fullTVShow.getHomepage());
                            Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
                            startActivity(webIntent);
                        }
                    });
                }
                seasons.setLayoutManager(new LinearLayoutManager(TVShowDetailsActivity.this,
                        LinearLayoutManager.HORIZONTAL, false));
                seasons.setAdapter(new HorizontalImageAdapter(fullTVShow.getSeasons(),
                        (image) -> zoomImageFromThumb(image,
                                ((BitmapDrawable) image.getDrawable()).getBitmap())));
            }
        });

    }


    private void useIntentInfo() {
        toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(tvShow.getTitle());
        ratingBar = findViewById(R.id.ratingBar);
        toolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBitmapDrawable != null) {
                    zoomImageFromThumb(v, mBitmapDrawable.getBitmap());
                }
            }
        });
        ratingBar.setRating(tvShow.getVoteAverage().floatValue() / 2);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    onInfoUpdatedListener = new OnInfoUpdatedListener() {
                        @Override
                        public void OnInfoUpdated(float rating) {
                            ratingBar.setRating(rating);
                            Snackbar.make(findViewById(R.id.nested_scroll_view), getString(R.string.rating_saved), Snackbar.LENGTH_LONG).show();
                        }
                    };
                    repository.addOnInfoUpdatedListener(onInfoUpdatedListener);
                    repository.rateTVShow(tvShow.getId(), rating * 2);
                }
            }
        });


        voteCountView.setText(getString(R.string.vote_count, tvShow.getVoteAverage(), tvShow.getVoteCount()));
        overviewView.setText(tvShow.getOverview());
        releaseDateView.setVisibility(View.GONE);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setQuote(tvShow.getTitle() + "     \r\nPlot: " + tvShow.
                                getOverview())
                        .setContentUrl(Uri.parse("https://image.tmdb.org/t/p/w500" + tvShow.
                                getPosterPath()))
                        .build();
                ShareDialog.show(TVShowDetailsActivity.this, shareLinkContent);
            }
        });
        Picasso
                .with(this)
                .load("https://image.tmdb.org/t/p/w500" + tvShow.getPosterPath())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mBitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                        mBitmapDrawable.setGravity(Gravity.NO_GRAVITY);
                        toolbarLayout.setBackground(mBitmapDrawable);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Log.d("TAG", "FAILED");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Log.d("TAG", "Prepare Load");
                    }
                });
    }

    private void getIntentInfo() {
        if (getIntent() != null && tvShow == null) {
            tvShow = (TVEntity) getIntent().getExtras().getSerializable(TV_ENTITY);
        }
        watched.setImageResource((dbService.checkIfExists(tvShow.getId())) ? R.mipmap.checked : R.mipmap.not_checked);
        watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (helperService.toDoWithWatched(tvShow)) {
                    case WATCHED:
                        watched.setImageResource(R.mipmap.checked);
                        Snackbar.make(findViewById(R.id.nested_scroll_view), getString(R.string.added_to_watched), Snackbar.LENGTH_SHORT).show();
                        break;
                    case FAVOURITE:
                        Snackbar.make(findViewById(R.id.nested_scroll_view), getString(R.string.you_cant_favourrite), Snackbar.LENGTH_SHORT).show();
                        break;
                    case CANCELED:
                        AlertDialog.Builder builder = new AlertDialog.Builder(TVShowDetailsActivity.this);
                        builder.setMessage(getString(R.string.confirm))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbService.deleteFromDb(tvShow.getId());
                                        watched.setImageResource(R.mipmap.not_checked);
                                        Snackbar.make(findViewById(R.id.nested_scroll_view), getString(R.string.mark_unwatched), Snackbar.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.create().show();
                        break;

                }
            }
        });
        if (dbService.checkIfIsFavourite(tvShow.getId())) {
            fab.setImageResource(R.drawable.ic_stars_black_24dp);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_movie_details);
        seasons = findViewById(R.id.genres);
        links = findViewById(R.id.links);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        watched = findViewById(R.id.watched);
        voteCountView = findViewById(R.id.vote_count);
        overviewView = findViewById(R.id.overview);
        releaseDateView = findViewById(R.id.release_date);
        share = findViewById(R.id.share);
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helperService.toDoWithFavourite(tvShow)) {
                    fab.setImageResource(R.drawable.ic_stars_black_24dp);
                    Snackbar.make(v, getString(R.string.added_to_favourite), Snackbar.LENGTH_SHORT)
                            .show();
                    watched.setImageResource(R.mipmap.checked);
                } else {
                    fab.setImageResource(R.drawable.ic_star_border_black_24dp);
                    Snackbar.make(v, getString(R.string.removed_from_favourite), Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void zoomImageFromThumb(final View thumbView, Bitmap bitmap) {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        final ImageView expandedImageView = findViewById(R.id.expanded_image);
        expandedImageView.setImageBitmap(bitmap);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}

