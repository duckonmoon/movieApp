package movies.test.softserve.movies.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import movies.test.softserve.movies.fragment.AchievementsFragment;
import movies.test.softserve.movies.R;
import movies.test.softserve.movies.fragment.SearchFragment;
import movies.test.softserve.movies.adapter.MovieListWrapper;
import movies.test.softserve.movies.adapter.MovieRecyclerViewAdapter;
import movies.test.softserve.movies.controller.MainController;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.event.AddedItemsEvent;
import movies.test.softserve.movies.fragment.GenreFragment;
import movies.test.softserve.movies.fragment.MovieFragment;
import movies.test.softserve.movies.fragment.TVShowFragment;
import movies.test.softserve.movies.fragment.WatchedFragment;
import movies.test.softserve.movies.service.DBHelperService;
import movies.test.softserve.movies.service.DBMovieService;
import movies.test.softserve.movies.service.RatingService;
import movies.test.softserve.movies.service.StartActivityClass;
import movies.test.softserve.movies.viewholder.MainViewHolder;
import movies.test.softserve.movies.viewmodel.FragmentViewModel;

public class MoviesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";

    private RecyclerView mRecyclerView;

    private AddedItemsEvent event;

    private FragmentViewModel viewModel;

    private String errorMessage;

    private RatingService.OnRatingChangeListener onRatingChangeListener;

    private DBHelperService helperService = new DBHelperService();
    private DBMovieService dbService = DBMovieService.getInstance();
    private MainController mainController = MainController.getInstance();
    private RatingService ratingService = RatingService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = new AddedItemsEvent() {
            @Override
            public void onItemsAdded(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        errorMessage = message;
                    }
                });
            }
        };
        mainController.setAddedItemsEventListener(event);
        setContentView(R.layout.activity_movies);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(new MovieListWrapper(new MovieRecyclerViewAdapter(mainController.getMovies(),
                new MovieRecyclerViewAdapter.OnMovieSelect() {
                    @Override
                    public void OnMovieSelected(TVEntity mov) {
                        StartActivityClass.startMovieDetailsActivity(MoviesListActivity.this, (Movie) mov);
                    }
                }, new MovieRecyclerViewAdapter.OnFavouriteClick() {
            @Override
            public void onFavouriteClick(final TVEntity mov) {
                if (helperService.toDoWithFavourite((Movie) mov)) {
                    Snackbar.make(mRecyclerView, "Added to favourite",
                            Snackbar.LENGTH_LONG).show();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    new AlertDialog.Builder(MoviesListActivity.this)
                            .setMessage(R.string.delete_from_watched)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dbService.deleteFromDb(mov.getId());
                                    Snackbar.make(mRecyclerView, "Deleted from favourite",
                                            Snackbar.LENGTH_LONG).show();
                                    mRecyclerView.getAdapter().notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dbService.cancelFavourite(mov.getId());
                                    Snackbar.make(mRecyclerView, "Deleted from favourite",
                                            Snackbar.LENGTH_LONG).show();
                                    mRecyclerView.getAdapter().notifyDataSetChanged();
                                }
                            }).create()
                            .show();
                }
            }
        }), new MovieListWrapper.OnEndReachListener() {
            @Override
            public void onEndReach(MainViewHolder mholder) {
                final MovieListWrapper.ViewHolder holder = (MovieListWrapper.ViewHolder) mholder;
                if (errorMessage == null) {
                    mainController.requestMore();
                } else {
                    holder.mProgressBar.setVisibility(View.GONE);
                    holder.mButton.setVisibility(View.VISIBLE);
                    holder.mButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mainController.requestMore();
                            holder.mButton.setVisibility(View.GONE);
                            holder.mProgressBar.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }));
        viewModel = ViewModelProviders.of(this).get(FragmentViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (event == null) {
            event = new AddedItemsEvent() {
                @Override
                public void onItemsAdded(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorMessage = message;
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });
                }
            };
            mainController.setAddedItemsEventListener(event);
        }

        if (onRatingChangeListener == null) {
            onRatingChangeListener = new RatingService.OnRatingChangeListener() {
                @Override
                public void onRatingChange(RatingService.Levels lvl, Float rating) {
                    navigationMenuStart();
                }
            };
            ratingService.addOnRatingChangeListener(onRatingChangeListener);
        }

        navigationMenuStart();

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.explore) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                transaction.remove(fragment);
            }
        } else if (id == R.id.favourite) {
            if (viewModel.getMovieFragment() == null) {
                viewModel.setMovieFragment(new MovieFragment());
            }
            transaction.replace(R.id.constraint_layout, viewModel.getMovieFragment());
        } else if (id == R.id.watched) {
            if (viewModel.getWatchedFragment() == null) {
                viewModel.setWatchedFragment(new WatchedFragment());
            }
            transaction.replace(R.id.constraint_layout, viewModel.getWatchedFragment());
        } else if (id == R.id.genres) {
            if (viewModel.getGenresFragment() == null) {
                viewModel.setGenresFragment(new GenreFragment());
            }
            transaction.replace(R.id.constraint_layout, viewModel.getGenresFragment());
        } else if (id == R.id.tv_shows) {
            if (viewModel.getTvShowFragment() == null) {
                viewModel.setTvShowFragment(new TVShowFragment());
            }
            transaction.replace(R.id.constraint_layout, viewModel.getTvShowFragment());
        } else if (id == R.id.search) {
            if (viewModel.getSearchFragment() == null) {
                viewModel.setSearchFragment(new SearchFragment());
            }
            transaction.replace(R.id.constraint_layout, viewModel.getSearchFragment());
        } else if (id == R.id.achievements) {
            if (viewModel.getAchievementsFragment() == null) {
                viewModel.setAchievementsFragment(new AchievementsFragment());
            }
            transaction.replace(R.id.constraint_layout, viewModel.getAchievementsFragment());
        }
        transaction.commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        mainController.removeAddedItemsEventListener();
        if (onRatingChangeListener != null) {
            ratingService.removeOnRatingChangeListener(onRatingChangeListener);
            onRatingChangeListener = null;
        }

        event = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigationMenuStart() {
        View navigationView = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        ProgressBar progressBar = navigationView.findViewById(R.id.rating_service_rating);
        ImageView imageView = navigationView.findViewById(R.id.rating_service_image);
        progressBar.setProgress(ratingService.getProgress().intValue());
        switch (ratingService.getLvl()) {
            case ZERO:
                imageView.setImageResource(R.mipmap.zero_icom_round);
                break;
            case FIRST:
                imageView.setImageResource(R.mipmap.point_holderf_round);
                break;
            case SECOND:
                imageView.setImageResource(R.mipmap.ic_two_round);
                break;
            case THIRD:
                imageView.setImageResource(R.mipmap.ic_three);
                break;
            case FOURTH:
                imageView.setImageResource(R.mipmap.ic_four);
                break;
            case FIFTH:
                imageView.setImageResource(R.mipmap.ic_five);
                break;
            case SIXTH:
                imageView.setImageResource(R.mipmap.ic_six);
                break;
            case SEVENTH:
                imageView.setImageResource(R.mipmap.ic_seven);
                break;
            case EIGHTH:
                imageView.setImageResource(R.mipmap.ic_eight);
                break;
            case NINTH:
                imageView.setImageResource(R.mipmap.ic_nine);
                break;
        }
    }
}


