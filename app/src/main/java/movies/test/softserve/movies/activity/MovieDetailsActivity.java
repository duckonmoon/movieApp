package movies.test.softserve.movies.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import movies.test.softserve.movies.service.DBService;
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
    private DBService dbService;

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

    FullMovieViewModel viewModel;

    private OnMovieInformationGet listener;
    private OnInfoUpdatedListener infoListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        viewModel = ViewModelProviders.of(this).get(FullMovieViewModel.class);
        dbService = DBService.getInstance();
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
        if (dbService.checkIfMovieIsFavourite(viewModel.getMovie().getId())) {
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
        watched.setImageResource(DBService.getInstance().checkIfMovieExists(viewModel.getMovie().getId()) ? R.mipmap.checked : R.mipmap.not_checked);
        watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DBService.getInstance().checkIfMovieExists(viewModel.getMovie().getId())) {
                    watched.setImageResource(R.mipmap.checked);
                    DBService.getInstance().addMovieToDb(viewModel.getMovie().getId(),
                            viewModel.getMovie().getTitle(),
                            viewModel.getMovie().getVoteAverage().floatValue(),
                            viewModel.getMovie().getVoteCount(),
                            viewModel.getMovie().getOverview(),
                            viewModel.getMovie().getReleaseDate(),
                            viewModel.getMovie().getPosterPath()
                    );
                    Snackbar.make(findViewById(R.id.nested_scroll_view), "Added to watched", Snackbar.LENGTH_SHORT).show();
                }
                else{

                    if (DBService.getInstance().checkIfMovieIsFavourite(viewModel.getMovie().getId())){
                        Snackbar.make(findViewById(R.id.nested_scroll_view), "It's favourite, u cant do this", Snackbar.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this);
                        builder.setMessage(R.string.confirm)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DBService.getInstance().deleteMovieFromDb(viewModel.getMovie().getId());
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
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(findViewById(R.id.nested_scroll_view), "Isn't ready :)", Snackbar.LENGTH_LONG).show();
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
                    Snackbar.make(findViewById(R.id.nested_scroll_view), "Isn't ready :)", Snackbar.LENGTH_LONG).show();
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
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(findViewById(R.id.nested_scroll_view), "Isn't ready :)", Snackbar.LENGTH_LONG).show();
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
                if (!dbService.checkIfMovieIsFavourite(viewModel.getMovie().getId())) {
                    if (!dbService.checkIfMovieExists(viewModel.getMovie().getId())) {
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
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(MovieDetailsActivity.this.getResources(), bitmap);
                            bitmapDrawable.setGravity(Gravity.NO_GRAVITY);
                            toolbarLayout.setBackground(bitmapDrawable);
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
}
