package movies.test.softserve.movies.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.MovieListAdapter;
import movies.test.softserve.movies.constans.Constants;
import movies.test.softserve.movies.controller.MainController;
import movies.test.softserve.movies.entity.LoginSession;
import movies.test.softserve.movies.event.OnLoginSessionGetListener;
import movies.test.softserve.movies.fragment.MovieFragment;
import movies.test.softserve.movies.fragment.WatchedFragment;
import movies.test.softserve.movies.service.MovieService;
import movies.test.softserve.movies.viewmodel.FragmentViewModel;

public class MoviesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";

    private RecyclerView mRecyclerView;
    private MovieListAdapter mMovieListAdapter;

    private FragmentViewModel viewModel;

    private Menu menu;

    private OnLoginSessionGetListener onLoginSessionGetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMovieListAdapter = new MovieListAdapter(this);
        mRecyclerView.setAdapter(mMovieListAdapter);
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
        mMovieListAdapter.notifyDataSetChanged();
        if (onLoginSessionGetListener == null) {
            onLoginSessionGetListener = new OnLoginSessionGetListener() {
                @Override
                public void OnLoginSessionGet(@NotNull LoginSession loginSession) {

                    menu.findItem(R.id.login).setVisible(false);
                    menu.findItem(R.id.log_out).setVisible(true);

                }
            };
            MovieService.getInstance().addOnLoginSessionGetListener(onLoginSessionGetListener);
        }
        Uri uri = getIntent().getData();
        if (uri != null) {
            if (uri.getQueryParameter("approved") != null) {
                MainController.getInstance().isTokenApproved();
                MovieService.getInstance().tryToGetLoginSession();
            } else {
                Snackbar.make(mRecyclerView, "Denied!!!", Snackbar.LENGTH_SHORT).show();
            }
        }
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
        }
        transaction.commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_movie_list_menu, menu);
        menu.findItem(R.id.login).setVisible(MainController.getInstance().getLoginSession() == null);
        menu.findItem(R.id.log_out).setVisible(MainController.getInstance().getLoginSession() != null);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                MainController controller = MainController.getInstance();
                if (controller.getAppToken() != null) {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(Constants.AUTHENTICATE + MainController.getInstance().getAppToken().getRequestToken() + Constants.REDIRECT_URI));
                    startActivity(intent);
                } else {
                    Toast.makeText(controller.getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.log_out:
                MainController.getInstance().LogOut();
                menu.findItem(R.id.login).setVisible(true);
                menu.findItem(R.id.log_out).setVisible(false);
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MovieService.getInstance().removeOnLoginSessionGetListener(onLoginSessionGetListener);
    }
}
