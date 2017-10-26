package movies.test.softserve.movies.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.event.OnMovieInformationGet;
import movies.test.softserve.movies.service.MovieService;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String RELEASE_DATE = "release date";
    public static final String VOTE_AVERAGE = "vote average";
    public static final String VOTE_COUNT = "vote count";
    public static final String POSTER_PATH = "poster path";
    public static final String OVERVIEW = "overview";

    private MovieService service;

    private Integer id;
    private String title;
    private String releaseDate;
    private Double voteAverage;
    private Integer voteCount;
    private String posterPath;
    private String overview;
    private FullMovie fullMovie;

    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionButton fab;
    private TextView overViewView;
    private RatingBar ratingBar;
    private TextView voteCountView;
    private TextView releaseDateView;
    private LinearLayout genres;
    private LinearLayout countries;
    private LinearLayout companies;


    private OnMovieInformationGet listener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        initView();
        getIntentInfo();
        useIntentInfo();

    }

    @Override
    protected void onResume(){
        super.onResume();
        getFullInfo();
    }

    private void useIntentInfo() {
        getSupportActionBar().setTitle(title);
        overViewView.setText(overview);
        ratingBar.setRating(voteAverage.floatValue() / 2);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                Snackbar.make(findViewById(R.id.nested_scroll_view), "Your rating saved", Snackbar.LENGTH_LONG).show();
            }
        });
        releaseDateView.setText(releaseDateView.getText().toString() + releaseDate);
        voteCountView.setText("" + voteAverage+ "/" + voteCount);


    }




    private void getFullInfo() {
        if (listener==null) {
            service = MovieService.getInstance();
            listener = new OnMovieInformationGet() {
                @Override
                public void onMovieGet(FullMovie movie) {
                    fullMovie = movie;
                    for (int i = 0; i < fullMovie.getGenres().size(); i++) {
                        Button button = new Button(new ContextThemeWrapper(MovieDetailsActivity.this.getBaseContext(), R.style.Widget_AppCompat_Button_Borderless_Colored));
                        button.setBackgroundColor(Color.TRANSPARENT);
                        button.setText(fullMovie.getGenres().get(i).getName());
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(findViewById(R.id.nested_scroll_view),"Isn't ready :)",Snackbar.LENGTH_LONG).show();
                            }
                        });
                        genres.addView(button);
                    }
                    for (int i = 0; i < fullMovie.getProductionCountries().size(); i++) {
                        Button button = new Button(new ContextThemeWrapper(MovieDetailsActivity.this.getBaseContext(), R.style.Widget_AppCompat_Button_Borderless_Colored));
                        button.setBackgroundColor(Color.TRANSPARENT);
                        button.setText(fullMovie.getProductionCountries().get(i).getName());
                        button.setPadding(0,0,50,0);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(findViewById(R.id.nested_scroll_view),"Isn't ready :)",Snackbar.LENGTH_LONG).show();
                            }
                        });
                        countries.addView(button);
                    }
                    for (int i = 0; i < fullMovie.getProductionCompanies().size(); i++) {
                        Button button = new Button(new ContextThemeWrapper(MovieDetailsActivity.this.getBaseContext(), R.style.Widget_AppCompat_Button_Borderless_Colored));
                        button.setBackgroundColor(Color.TRANSPARENT);
                        button.setText(fullMovie.getProductionCompanies().get(i).getName());
                        button.setPadding(0,0,50,0);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(findViewById(R.id.nested_scroll_view),"Isn't ready :)",Snackbar.LENGTH_LONG).show();
                            }
                        });
                        companies.addView(button);
                    }
                }
            };
            service.addListener(listener);

            service.tryToGetMovie(id);
        }
    }

    private void initView() {
        overViewView = findViewById(R.id.overview);
        toolbar = findViewById(R.id.toolbar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Added to favourite", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ratingBar = findViewById(R.id.ratingBar);
        voteCountView = findViewById(R.id.vote_count);
        releaseDateView = findViewById(R.id.release_date);
        genres = findViewById(R.id.genres);
        countries = findViewById(R.id.countries);
        companies = findViewById(R.id.companies);

    }

    private void getIntentInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            id = bundle.getInt(ID);
            title = bundle.getString(TITLE);
            releaseDate = bundle.getString(RELEASE_DATE);
            voteAverage = bundle.getDouble(VOTE_AVERAGE);
            voteCount = bundle.getInt(VOTE_COUNT);
            posterPath = bundle.getString(POSTER_PATH);
            overview = bundle.getString(OVERVIEW);
            Picasso
                    .with(this)
                    .load("https://image.tmdb.org/t/p/w500" + posterPath)
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
    protected void onPause(){
        super.onPause();
        service.removeListener(listener);
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
}
