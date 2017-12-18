package movies.test.softserve.movies.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.MovieListWrapper;
import movies.test.softserve.movies.adapter.MovieRecyclerViewAdapter;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.event.OnListOfMoviesGetListener;
import movies.test.softserve.movies.service.DBHelperService;
import movies.test.softserve.movies.service.DBMovieService;
import movies.test.softserve.movies.service.MovieService;
import movies.test.softserve.movies.util.StartActivityClass;
import movies.test.softserve.movies.viewmodel.PageViewModel;

public class SearchActivity extends BaseActivity {

    public static final String ID = "search_id";
    public static final String SEARCH_PARAM = "search_param";
    public static final String NAME = "name";
    public static final String GENRES = "genres";
    public static final String COMPANIES = "companies";
    public static final String COUNTRIES = "countries";

    private int id = -1;

    private RecyclerView mRecyclerView;

    private PageViewModel mPageViewModel;

    private Handler handler;


    private DBHelperService helperService = new DBHelperService();
    private MovieService movieService = MovieService.getInstance();
    private DBMovieService dbService = DBMovieService.getInstance();
    private String message = null;
    private OnListOfMoviesGetListener onListOfMoviesGetListener = new OnListOfMoviesGetListener() {
        @Override
        public void onListOfMoviesGetListener(@NonNull List<? extends TVEntity> movies) {
            if (movies.size() > 0) {
                mPageViewModel.getList().addAll(movies);
                mPageViewModel.setPage(mPageViewModel.getPage() + 1);
            } else {
                message = "all";
            }
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        handler = new Handler();
        try {
            setTitle(getIntent().getStringExtra(NAME));
        } catch (Exception ignored) {
        }
        mPageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        mRecyclerView = findViewById(R.id.recyclerview);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        }
        mRecyclerView.setAdapter(new MovieListWrapper(new MovieRecyclerViewAdapter(mPageViewModel.getList(), new MovieRecyclerViewAdapter.OnMovieSelect() {
            @Override
            public void OnMovieSelected(TVEntity mov) {
                StartActivityClass.startDetailsActivity(SearchActivity.this, mov);
            }
        }, new MovieRecyclerViewAdapter.OnFavouriteClick() {
            @Override
            public void onFavouriteClick(final TVEntity movie) {
                new Thread(() -> {
                    if (helperService.toDoWithFavourite(movie)) {
                        handler.post(() -> {
                            Snackbar.make(mRecyclerView, getString(R.string.added_to_favourite), Snackbar.LENGTH_SHORT)
                                    .show();
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        });

                    } else {
                        handler.post(() -> buildAlertDialog(movie));

                    }
                }).start();

            }
        }), new MovieListWrapper.OnEndReachListener() {
            @Override
            public MovieListWrapper.State onEndReach() {
                if (message == null) {
                    switch (getIntent().getStringExtra(SEARCH_PARAM)) {
                        case GENRES:
                            movieService.getMovieByGenreCompany(id, null, mPageViewModel.getPage());
                            break;
                        case COMPANIES:
                            movieService.getMovieByGenreCompany(null, id, mPageViewModel.getPage());
                            break;
                        case COUNTRIES:
                            break;
                        default:
                            Log.e("Smth went wrong", "Nothing selected");
                            break;
                    }
                    return MovieListWrapper.State.Loading;
                } else {
                    return MovieListWrapper.State.end;
                }
            }

            @Override
            public void onEndButtonClick() {

            }
        }));

        Intent intent = getIntent();
        id = intent.getIntExtra(ID, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        movieService.addOnListOfMoviesGetListener(onListOfMoviesGetListener);

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        movieService.removeOnListOfMoviesGetListener(onListOfMoviesGetListener);
    }

    private void buildAlertDialog(final TVEntity movie) {
        new AlertDialog.Builder(SearchActivity.this)
                .setMessage(R.string.delete_from_watched)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbService.deleteFromDb(movie.getId());
                        Snackbar.make(mRecyclerView, R.string.mark_unwatched,
                                Snackbar.LENGTH_LONG).show();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbService.cancelFavourite(movie.getId());
                        Snackbar.make(mRecyclerView, R.string.removed_from_favourite,
                                Snackbar.LENGTH_LONG).show();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                }).create()
                .show();
    }
}
