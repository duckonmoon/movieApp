package movies.test.softserve.movies.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.MovieListWrapper;
import movies.test.softserve.movies.adapter.MovieRecyclerViewAdapter;
import movies.test.softserve.movies.controller.MainController;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.event.AddedItemsEvent;
import movies.test.softserve.movies.fragment.AchievementsFragment;
import movies.test.softserve.movies.fragment.GenreFragment;
import movies.test.softserve.movies.fragment.MovieFragment;
import movies.test.softserve.movies.fragment.SearchFragment;
import movies.test.softserve.movies.fragment.TVShowFragment;
import movies.test.softserve.movies.fragment.WatchedFragment;
import movies.test.softserve.movies.service.DBHelperService;
import movies.test.softserve.movies.service.DbMovieServiceRoom;
import movies.test.softserve.movies.util.RatingService;
import movies.test.softserve.movies.util.StartActivityClass;
import movies.test.softserve.movies.view.RatingView;
import movies.test.softserve.movies.viewmodel.FragmentViewModel;

public class MoviesListActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";

    private RecyclerView mRecyclerView;
    private FragmentViewModel viewModel;
    private String errorMessage;
    private AddedItemsEvent event = (message) ->
            runOnUiThread(() -> {
                errorMessage = message;
                mRecyclerView.getAdapter().notifyDataSetChanged();
            });
    private DBHelperService helperService = new DBHelperService();
    private DbMovieServiceRoom dbService = DbMovieServiceRoom.Companion.getInstance();
    private MainController mainController = MainController.getInstance();
    private RatingService ratingService = RatingService.getInstance();
    private RatingService.OnRatingChangeListener onRatingChangeListener
            = (lvl, rating) -> navigationMenuStart();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        mRecyclerView = findViewById(R.id.recyclerview);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        }


        mRecyclerView.setAdapter(new MovieListWrapper(new MovieRecyclerViewAdapter(mainController.getMovies(),
                new MovieRecyclerViewAdapter.OnMovieSelect() {
                    @Override
                    public void OnMovieSelected(TVEntity mov) {
                        StartActivityClass.startDetailsActivity(MoviesListActivity.this, mov);
                    }
                }, new MovieRecyclerViewAdapter.OnFavouriteClick() {
            @Override
            public void onFavouriteClick(final TVEntity mov, final Integer position) {
                new Thread(() -> {
                    if (helperService.toDoWithFavourite(mov)) {
                        runOnUiThread(() -> {
                            Snackbar.make(mRecyclerView, getString(R.string.added_to_favourite),
                                    Snackbar.LENGTH_LONG).show();
                            mRecyclerView.getAdapter().notifyItemChanged(position);
                        });

                    } else {
                        runOnUiThread(() -> {
                            new AlertDialog.Builder(MoviesListActivity.this)
                                    .setMessage(R.string.delete_from_watched)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            new Thread(() -> dbService.deleteFromDb(mov)).start();

                                            Snackbar.make(mRecyclerView, R.string.mark_unwatched,
                                                    Snackbar.LENGTH_LONG).show();
                                            mRecyclerView.getAdapter().notifyItemChanged(position);
                                        }
                                    })
                                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            new Thread(() -> dbService.cancelFavourite(mov)).start();
                                            Snackbar.make(mRecyclerView, R.string.removed_from_favourite,
                                                    Snackbar.LENGTH_LONG).show();
                                            mRecyclerView.getAdapter().notifyItemChanged(position);
                                        }
                                    }).create()
                                    .show();
                        });
                    }
                }).start();

            }
        }), new MovieListWrapper.OnEndReachListener() {
            @Override
            public MovieListWrapper.State onEndReach() {
                if (errorMessage == null) {
                    mainController.requestMore();
                    return MovieListWrapper.State.Loading;
                } else {
                    return MovieListWrapper.State.Failed;
                }
            }

            @Override
            public void onEndButtonClick() {
                mainController.requestMore();
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
        mainController.setAddedItemsEventListener(event);

        ratingService.addOnRatingChangeListener(onRatingChangeListener);


        navigationMenuStart();

        if (mainController.check(this)) {
            mRecyclerView.getAdapter().notifyDataSetChanged();
            mainController.unCheck(this);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            if (fragmentList.size() > 0){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                for (Fragment fragment
                        : fragmentList){
                    transaction.remove(fragment);
                }
                transaction.commit();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
        ratingService.removeOnRatingChangeListener(onRatingChangeListener);

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
            case R.id.btn_sign_out:
                StartActivityClass.startActivitySignOut(this);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigationMenuStart() {
        View navigationView = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        ProgressBar progressBar = navigationView.findViewById(R.id.rating_service_rating);
        RatingView ratingView = navigationView.findViewById(R.id.rating_service_image);
        ratingView.setLevel(ratingService.getLvl().ordinal() + 1);
        progressBar.setProgress(ratingService.getProgress().intValue());
    }
}


