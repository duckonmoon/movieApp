package movies.test.softserve.movies.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.HorizontalButtonAdapter;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.Genre;
import movies.test.softserve.movies.entity.ProductionCompany;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.event.OnInfoUpdatedListener;
import movies.test.softserve.movies.event.OnMovieInformationGet;
import movies.test.softserve.movies.service.DBHelperService;
import movies.test.softserve.movies.service.DBMovieService;
import movies.test.softserve.movies.service.MovieService;
import movies.test.softserve.movies.util.BudgetFormatter;
import movies.test.softserve.movies.util.StartActivityClass;
import movies.test.softserve.movies.viewmodel.FullMovieViewModel;

public class MovieDetailsActivity extends BaseActivity {

    public static final String TV_ENTITY = "tv entity";

    private MovieService service = MovieService.getInstance();
    private DBMovieService dbService = DBMovieService.getInstance();
    private DBHelperService helperService = new DBHelperService();


    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionButton fab;
    private TextView overViewView;
    private RatingBar ratingBar;
    private TextView voteCountView;
    private TextView releaseDateView;
    private TextView links;
    private ImageView share;
    private ImageView watched;

    private Button trailersButton;

    private FullMovieViewModel viewModel;
    private TVEntity movie;


    private OnMovieInformationGet listener;
    private OnInfoUpdatedListener infoListener = new OnInfoUpdatedListener() {
        @Override
        public void OnInfoUpdated(float code) {
            ratingBar.setRating(code);
            Snackbar.make(findViewById(R.id.nested_scroll_view), getString(R.string.rating_saved), Snackbar.LENGTH_LONG).show();
        }
    };

    private Animator mCurrentAnimator;
    private BitmapDrawable mBitmapDrawable;

    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        viewModel = ViewModelProviders.of(this).get(FullMovieViewModel.class);
        initView();
        getIntentInfo();
        useIntentInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        service.addOnInfoUpdatedListener(infoListener);
        getFullInfo();
    }

    private void useIntentInfo() {
        getSupportActionBar().setTitle(movie.getTitle());
        overViewView.setText(movie.getOverview());
        ratingBar.setRating(movie.getVoteAverage().floatValue() / 2);
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                service.rateMovie(movie.getId(), rating * 2);
            }
        });
        releaseDateView.setText(getString(R.string.release_date, movie.getReleaseDate()));
        voteCountView.setText(getString(R.string.vote_count, movie.getVoteAverage(), movie.getVoteCount()));
        if (dbService.checkIfIsFavourite(movie.getId())) {
            fab.setImageResource(R.drawable.ic_stars_black_24dp);
        }

        share.setOnClickListener(v -> {
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setQuote(movie.getTitle() + "     \r\nPlot: " + movie.getOverview())
                    .setContentUrl(Uri.parse("https://image.tmdb.org/t/p/w500" + movie.getPosterPath()))
                    .build();
            ShareDialog.show(MovieDetailsActivity.this, shareLinkContent);
        });
        watched.setImageResource(dbService.checkIfExists(movie.getId()) ? R.mipmap.checked : R.mipmap.not_checked);
        watched.setOnClickListener(v -> {
            switch (helperService.toDoWithWatched(movie)) {
                case WATCHED:
                    watched.setImageResource(R.mipmap.checked);
                    Snackbar.make(findViewById(R.id.nested_scroll_view), getString(R.string.added_to_watched), Snackbar.LENGTH_SHORT).show();
                    break;
                case FAVOURITE:
                    Snackbar.make(findViewById(R.id.nested_scroll_view), getString(R.string.you_cant_favourrite), Snackbar.LENGTH_SHORT).show();
                    break;
                case CANCELED:
                    AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this);
                    builder.setMessage(R.string.confirm)
                            .setPositiveButton(R.string.yes, (dialog, id) -> {
                                dbService.deleteFromDb(movie.getId());
                                watched.setImageResource(R.mipmap.not_checked);
                                Snackbar.make(findViewById(R.id.nested_scroll_view), getString(R.string.mark_unwatched), Snackbar.LENGTH_SHORT).show();
                            })
                            .setNegativeButton(R.string.no, (dialog, id) -> {
                            });
                    builder.create().show();
                    break;
            }
        });

        trailersButton.setOnClickListener(view -> StartActivityClass.startVideosActivity(this,movie));
    }

    private void getFullInfo() {
        if (listener == null) {
            if (viewModel.getFullMovie() == null) {
                listener = new OnMovieInformationGet() {
                    @Override
                    public void onMovieGet(FullMovie movie) {
                        viewModel.setFullMovie(movie);
                        addGenresCountriesCompanies();
                    }
                };
                service.addListener(listener);
                service.tryToGetMovie(movie.getId());
            } else {
                addGenresCountriesCompanies();
            }
        }
    }


    public void addGenresCountriesCompanies() {
        final FullMovie fullMovie = viewModel.getFullMovie();
        TextView budget = findViewById(R.id.budget);
        RecyclerView genres = findViewById(R.id.genres);
        RecyclerView countries = findViewById(R.id.countries);
        RecyclerView companies = findViewById(R.id.companies);
        if (fullMovie.getBudget() != 0) {
            budget.setText(getString(R.string.budget, BudgetFormatter.toMoney(fullMovie.getBudget())));
            budget.setVisibility(View.VISIBLE);
        }
        genres.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        genres.setAdapter(new HorizontalButtonAdapter(fullMovie.getGenres(),
                (i) -> StartActivityClass.startActivitySearch(this, (Genre) i)));

        countries.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        countries.setAdapter(new HorizontalButtonAdapter(fullMovie.getProductionCountries(),
                (i) -> {
                }));

        companies.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        companies.setAdapter(new HorizontalButtonAdapter(fullMovie.getProductionCompanies(),
                item -> StartActivityClass.startActivitySearch(this, (ProductionCompany) item)));


        if (fullMovie.getHomepage() != null && !fullMovie.getHomepage().equals("")) {
            links.setText(getString(R.string.homepage, fullMovie.getHomepage()));
            links.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartActivityClass.startWebIntent(MovieDetailsActivity.this, fullMovie.getHomepage());
                }
            });
        }
    }

    private void initView() {
        overViewView = findViewById(R.id.overview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        watched = findViewById(R.id.watched);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (helperService.toDoWithFavourite(movie)) {
                    fab.setImageResource(R.drawable.ic_stars_black_24dp);
                    Snackbar.make(view, getString(R.string.added_to_favourite), Snackbar.LENGTH_LONG)
                            .show();
                    watched.setImageResource(R.mipmap.checked);
                } else {
                    fab.setImageResource(R.drawable.ic_star_border_black_24dp);
                    Snackbar.make(view, R.string.removed_from_favourite, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
        ratingBar = findViewById(R.id.ratingBar);
        voteCountView = findViewById(R.id.vote_count);
        releaseDateView = findViewById(R.id.release_date);
        links = findViewById(R.id.links);
        share = findViewById(R.id.share);
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        toolbarLayout.setOnClickListener(v -> {
            if (mBitmapDrawable != null) {
                zoomImageFromThumb(toolbarLayout, mBitmapDrawable.getBitmap());
            }
        });
        trailersButton = findViewById(R.id.trailers_button);
        trailersButton.setVisibility(View.VISIBLE);
    }

    private void getIntentInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            movie = (TVEntity) intent.getExtras().getSerializable(TV_ENTITY);
            Picasso
                    .with(this)
                    .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mBitmapDrawable = new BitmapDrawable(MovieDetailsActivity.this.getResources(), bitmap);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (listener != null) {
            service.removeListener(listener);
            listener = null;
        }
        service.removeOnInfoUpdatedListener(infoListener);
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
