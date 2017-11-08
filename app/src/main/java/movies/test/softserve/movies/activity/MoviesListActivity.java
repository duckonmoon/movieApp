package movies.test.softserve.movies.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.MyMovieListWrapper;
import movies.test.softserve.movies.adapter.MyMovieRecyclerViewAdapter;
import movies.test.softserve.movies.controller.MainController;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.event.AddedItemsEvent;
import movies.test.softserve.movies.fragment.GenreFragment;
import movies.test.softserve.movies.fragment.MovieFragment;
import movies.test.softserve.movies.fragment.TVShowFragment;
import movies.test.softserve.movies.fragment.WatchedFragment;
import movies.test.softserve.movies.service.DBService;
import movies.test.softserve.movies.viewholder.MainViewHolder;
import movies.test.softserve.movies.viewmodel.FragmentViewModel;

public class MoviesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";

    private RecyclerView mRecyclerView;

    private AddedItemsEvent event;

    private FragmentViewModel viewModel;

    private String errorMessage;

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
        MainController.getInstance().setAddedItemsEventListener(event);
        setContentView(R.layout.activity_movies);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(new MyMovieListWrapper(new MyMovieRecyclerViewAdapter(MainController.getInstance().getMovies(),
                new MyMovieRecyclerViewAdapter.OnMovieSelect() {
                    @Override
                    public void OnMovieSelected(Movie movie) {
                        Intent intent = new Intent(MoviesListActivity.this, MovieDetailsActivity.class);
                        intent.putExtra(MovieDetailsActivity.ID, movie.getId());
                        intent.putExtra(MovieDetailsActivity.TITLE, movie.getTitle());
                        intent.putExtra(MovieDetailsActivity.POSTER_PATH, movie.getPosterPath());
                        intent.putExtra(MovieDetailsActivity.RELEASE_DATE, movie.getReleaseDate());
                        intent.putExtra(MovieDetailsActivity.VOTE_COUNT, movie.getVoteCount());
                        intent.putExtra(MovieDetailsActivity.VOTE_AVERAGE, movie.getVoteAverage());
                        intent.putExtra(MovieDetailsActivity.OVERVIEW, movie.getOverview());
                        MoviesListActivity.this.startActivity(intent);
                    }
                }, new MyMovieRecyclerViewAdapter.OnFavouriteClick() {
            @Override
            public void onFavouriteClick(final Movie movie) {
                final DBService dbService = DBService.getInstance();
                if (dbService.checkIfMovieIsFavourite(movie.getId())) {
                    new AlertDialog.Builder(MoviesListActivity.this)
                            .setMessage(R.string.delete_from_watched)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dbService.deleteMovieFromDb(movie.getId());
                                    Snackbar.make(MoviesListActivity.this.findViewById(R.id.recyclerview), "Deleted from favourite", Snackbar.LENGTH_LONG).show();
                                    mRecyclerView.getAdapter().notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dbService.cancelFavourite(movie.getId());
                                    Snackbar.make(MoviesListActivity.this.findViewById(R.id.recyclerview), "Deleted from favourite", Snackbar.LENGTH_LONG).show();
                                    mRecyclerView.getAdapter().notifyDataSetChanged();
                                }
                            }).create()
                            .show();
                } else {
                    if (!dbService.checkIfMovieExists(movie.getId())) {
                        dbService.insertMovieToFavourite(movie.getId(),
                                movie.getTitle(),
                                movie.getVoteAverage().floatValue(),
                                movie.getVoteCount(),
                                movie.getOverview(),
                                movie.getReleaseDate(),
                                movie.getPosterPath());
                    } else {
                        dbService.setFavourite(movie.getId());
                    }
                    Snackbar.make(MoviesListActivity.this.findViewById(R.id.recyclerview), "Added to favourite", Snackbar.LENGTH_LONG).show();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }

            }
        }), new MyMovieListWrapper.OnEndReachListener() {
            @Override
            public void onEndReach(MainViewHolder mholder) {
                final MyMovieListWrapper.ViewHolder holder = (MyMovieListWrapper.ViewHolder) mholder;
                final MainController mainController = MainController.getInstance();
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
        if (event != null) {
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
            MainController.getInstance().setAddedItemsEventListener(event);
        }

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
            if (viewModel.getGenresFragment() == null){
                viewModel.setGenresFragment(new GenreFragment());
            }
            transaction.replace(R.id.constraint_layout,viewModel.getGenresFragment());
        } else if (id == R.id.tv_shows){
            if (viewModel.getTvShowFragment() == null){
                viewModel.setTvShowFragment(new TVShowFragment());
            }
            transaction.replace(R.id.constraint_layout,viewModel.getTvShowFragment());
        }
        transaction.commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        MainController.getInstance().removeAddedItemsEventListener();
        event = null;
    }
}


