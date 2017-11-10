package movies.test.softserve.movies.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.event.OnInfoUpdatedListener;
import movies.test.softserve.movies.event.OnMovieInformationGet;
import movies.test.softserve.movies.service.DBMovieService;
import movies.test.softserve.movies.service.MovieService;
import movies.test.softserve.movies.viewmodel.FullMovieViewModel;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String RELEASE_DATE = "release date";
    public static final String VOTE_AVERAGE = "vote average";
    public static final String VOTE_COUNT = "vote count";
    public static final String POSTER_PATH = "poster path";
    public static final String OVERVIEW = "overview";

    private MovieService service;
    private DBMovieService dbService;

    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionButton fab;
    private TextView overViewView;
    private RatingBar ratingBar;
    private TextView voteCountView;
    private TextView releaseDateView;
    private TextView links;
    private LinearLayout genres;
    private LinearLayout countries;
    private LinearLayout companies;
    private ImageView share;
    private ImageView watched;

    private FullMovieViewModel viewModel;

    private OnMovieInformationGet listener;
    private OnInfoUpdatedListener infoListener;

    private Animator mCurrentAnimator;
    BitmapDrawable mBitmapDrawable;

    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        viewModel = ViewModelProviders.of(this).get(FullMovieViewModel.class);
        dbService = DBMovieService.getInstance();
        initView();
        getIntentInfo();
        useIntentInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (infoListener == null) {
            infoListener = new OnInfoUpdatedListener() {
                @Override
                public void OnInfoUpdated(float code) {
                    ratingBar.setRating(code);
                    Snackbar.make(findViewById(R.id.nested_scroll_view), "Your rating saved", Snackbar.LENGTH_LONG).show();
                }
            };
            MovieService.getInstance().addOnInfoUpdatedListener(infoListener);
        }
        getFullInfo();
    }

    private void useIntentInfo() {
        getSupportActionBar().setTitle(viewModel.getMovie().getTitle());
        overViewView.setText(viewModel.getMovie().getOverview());
        ratingBar.setRating(viewModel.getMovie().getVoteAverage().floatValue() / 2);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    MovieService.getInstance().rateMovie(viewModel.getMovie().getId(), rating * 2);
                }
            }
        });
        releaseDateView.setText(releaseDateView.getText().toString() + viewModel.getMovie().getReleaseDate());
        voteCountView.setText("" + ((float) Math.round(viewModel.getMovie().getVoteAverage() * 10)) / 10 + "/" + viewModel.getMovie().getVoteCount());
        if (dbService.checkIfIsFavourite(viewModel.getMovie().getId())) {
            fab.setImageResource(R.drawable.ic_stars_black_24dp);
        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setQuote(viewModel.getMovie().getTitle() + "     \r\nPlot: " + viewModel.getMovie().getOverview())
                        .setContentUrl(Uri.parse("https://image.tmdb.org/t/p/w500" + viewModel.getMovie().getPosterPath()))
                        .build();
                ShareDialog.show(MovieDetailsActivity.this, shareLinkContent);
            }
        });
        watched.setImageResource(DBMovieService.getInstance().checkIfExists(viewModel.getMovie().getId()) ? R.mipmap.checked : R.mipmap.not_checked);
        watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DBMovieService.getInstance().checkIfExists(viewModel.getMovie().getId())) {
                    watched.setImageResource(R.mipmap.checked);
                    DBMovieService.getInstance().addMovieToDb(viewModel.getMovie().getId(),
                            viewModel.getMovie().getTitle(),
                            viewModel.getMovie().getVoteAverage().floatValue(),
                            viewModel.getMovie().getVoteCount(),
                            viewModel.getMovie().getOverview(),
                            viewModel.getMovie().getReleaseDate(),
                            viewModel.getMovie().getPosterPath()
                    );
                    Snackbar.make(findViewById(R.id.nested_scroll_view), "Added to watched", Snackbar.LENGTH_SHORT).show();
                } else {

                    if (DBMovieService.getInstance().checkIfIsFavourite(viewModel.getMovie().getId())) {
                        Snackbar.make(findViewById(R.id.nested_scroll_view), "It's favourite, u cant do this", Snackbar.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this);
                        builder.setMessage(R.string.confirm)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DBMovieService.getInstance().deleteFromDb(viewModel.getMovie().getId());
                                        watched.setImageResource(R.mipmap.not_checked);
                                        Snackbar.make(findViewById(R.id.nested_scroll_view), "Marked as unwatched", Snackbar.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        builder.create().show();
                    }
                }
            }
        });
    }

    private void getFullInfo() {
        if (listener == null) {
            if (viewModel.getFullMovie() == null) {
                service = MovieService.getInstance();
                listener = new OnMovieInformationGet() {
                    @Override
                    public void onMovieGet(FullMovie movie) {
                        viewModel.setFullMovie(movie);
                        addGenresCountriesCompanies();
                    }
                };
                service.addListener(listener);
                service.tryToGetMovie(viewModel.getMovie().getId());
            } else {
                addGenresCountriesCompanies();
            }
        }
    }

    public void addGenresCountriesCompanies() {
        for (int i = 0; i < viewModel.getFullMovie().getGenres().size(); i++) {
            Button button = new Button(MovieDetailsActivity.this);
            button.setTextColor(ContextCompat.getColor(MovieDetailsActivity.this, R.color.main_app_color));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setText(viewModel.getFullMovie().getGenres().get(i).getName());
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(findViewById(R.id.nested_scroll_view), "Isn't ready :)", Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(MovieDetailsActivity.this,SearchActivity.class);
                    intent.putExtra(SearchActivity.SEARCH_PARAM,SearchActivity.GENRES);
                    intent.putExtra(SearchActivity.ID,viewModel.getFullMovie().getGenres().get(finalI).getId());
                    intent.putExtra(SearchActivity.NAME,viewModel.getFullMovie().getGenres().get(finalI).getName());
                    startActivity(intent);
                }
            });
            genres.addView(button);
        }
        for (int i = 0; i < viewModel.getFullMovie().getProductionCountries().size(); i++) {
            Button button = new Button(MovieDetailsActivity.this);
            button.setTextColor(ContextCompat.getColor(MovieDetailsActivity.this, R.color.main_app_color));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setText(viewModel.getFullMovie().getProductionCountries().get(i).getName());
            button.setPadding(0, 0, 50, 0);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            countries.addView(button);
        }
        for (int i = 0; i < viewModel.getFullMovie().getProductionCompanies().size(); i++) {
            Button button = new Button(MovieDetailsActivity.this);
            button.setTextColor(ContextCompat.getColor(MovieDetailsActivity.this, R.color.main_app_color));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setText(viewModel.getFullMovie().getProductionCompanies().get(i).getName());
            button.setPadding(0, 0, 50, 0);
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MovieDetailsActivity.this,SearchActivity.class);
                    intent.putExtra(SearchActivity.SEARCH_PARAM,SearchActivity.GENRES);
                    intent.putExtra(SearchActivity.ID,viewModel.getFullMovie().getProductionCompanies().get(finalI).getId());
                    intent.putExtra(SearchActivity.NAME,viewModel.getFullMovie().getProductionCompanies().get(finalI).getName());
                    startActivity(intent);
                }
            });
            companies.addView(button);
        }
        if (viewModel.getFullMovie().getHomepage() != null && !viewModel.getFullMovie().getHomepage().equals("")) {
            links.setText(getString(R.string.homepage) + viewModel.getFullMovie().getHomepage());
            links.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri webPage = Uri.parse(viewModel.getFullMovie().getHomepage());
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
                    startActivity(webIntent);
                }
            });
        }
    }

    private void initView() {
        overViewView = findViewById(R.id.overview);
        toolbar = findViewById(R.id.toolbar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        watched = findViewById(R.id.watched);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dbService.checkIfIsFavourite(viewModel.getMovie().getId())) {
                    if (!dbService.checkIfExists(viewModel.getMovie().getId())) {
                        dbService.insertMovieToFavourite(viewModel.getMovie().getId(),
                                viewModel.getMovie().getTitle(),
                                viewModel.getMovie().getVoteAverage().floatValue(),
                                viewModel.getMovie().getVoteCount(),
                                viewModel.getMovie().getOverview(),
                                viewModel.getMovie().getReleaseDate(),
                                viewModel.getMovie().getPosterPath());
                    } else {
                        dbService.setFavourite(viewModel.getMovie().getId());
                    }
                    fab.setImageResource(R.drawable.ic_stars_black_24dp);
                    Snackbar.make(view, "Added to favourite", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    watched.setImageResource(R.mipmap.checked);
                } else {
                    dbService.cancelFavourite(viewModel.getMovie().getId());
                    fab.setImageResource(R.drawable.ic_star_border_black_24dp);
                    Snackbar.make(view, "Removed to favourite", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        ratingBar = findViewById(R.id.ratingBar);
        voteCountView = findViewById(R.id.vote_count);
        releaseDateView = findViewById(R.id.release_date);
        genres = findViewById(R.id.genres);
        countries = findViewById(R.id.countries);
        companies = findViewById(R.id.companies);
        links = findViewById(R.id.links);
        share = findViewById(R.id.share);
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        toolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(toolbarLayout,mBitmapDrawable);
            }
        });

    }

    private void getIntentInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            Movie movie = new Movie();
            Bundle bundle = intent.getExtras();
            movie.setId(bundle.getInt(ID));
            movie.setTitle(bundle.getString(TITLE));
            movie.setReleaseDate(bundle.getString(RELEASE_DATE));
            movie.setVoteAverage(bundle.getDouble(VOTE_AVERAGE));
            movie.setVoteCount(bundle.getInt(VOTE_COUNT));
            movie.setPosterPath(bundle.getString(POSTER_PATH));
            movie.setOverview(bundle.getString(OVERVIEW));
            viewModel.setMovie(movie);
            Picasso
                    .with(this)
                    .load("https://image.tmdb.org/t/p/w500" + viewModel.getMovie().getPosterPath())
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
            MovieService.getInstance().removeListener(listener);
        }
        if (infoListener != null) {
            MovieService.getInstance().removeOnInfoUpdatedListener(infoListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void zoomImageFromThumb(final View thumbView, BitmapDrawable bitmapDrawable) {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        final ImageView expandedImageView = findViewById(R.id.expanded_image);
        expandedImageView.setImageDrawable(bitmapDrawable);

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
