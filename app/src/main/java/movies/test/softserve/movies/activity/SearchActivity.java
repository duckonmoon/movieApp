package movies.test.softserve.movies.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.MyMovieListWrapper;
import movies.test.softserve.movies.adapter.MyMovieRecyclerViewAdapter;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.entity.TVShow;
import movies.test.softserve.movies.event.OnListOfMoviesGetListener;
import movies.test.softserve.movies.service.DBHelperService;
import movies.test.softserve.movies.service.DBMovieService;
import movies.test.softserve.movies.service.MovieService;
import movies.test.softserve.movies.service.StartActivityClass;
import movies.test.softserve.movies.viewholder.MainViewHolder;
import movies.test.softserve.movies.viewmodel.PageViewModel;

public class SearchActivity extends AppCompatActivity {

    public static final String ID = "search_id";
    public static final String SEARCH_PARAM = "search_param";
    public static final String NAME = "name";
    public static final String GENRES = "genres";
    public static final String COMPANIES = "companies";
    public static final String COUNTRIES = "countries";

    int id = -1;

    RecyclerView mRecyclerView;

    PageViewModel mPageViewModel;

    DBHelperService helperService = new DBHelperService();
    MovieService movieService = MovieService.getInstance();
    DBMovieService dbService = DBMovieService.getInstance();

    OnListOfMoviesGetListener onListOfMoviesGetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        try {
            setTitle(getIntent().getStringExtra(NAME));
        } catch (Exception ignored) {
        }
        mPageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MyMovieListWrapper(new MyMovieRecyclerViewAdapter(mPageViewModel.getList(), new MyMovieRecyclerViewAdapter.OnMovieSelect() {
            @Override
            public void OnMovieSelected(TVEntity mov) {
                StartActivityClass.startMovieDetailsActivity(SearchActivity.this, (Movie) mov);
            }
        }, new MyMovieRecyclerViewAdapter.OnFavouriteClick() {
            @Override
            public void onFavouriteClick(final TVEntity movie) {
                if (movie instanceof Movie){
                    if (helperService.toDoWithFavourite((Movie)movie)){
                        Snackbar.make(mRecyclerView,"Added to favourite",Snackbar.LENGTH_SHORT);
                    }else{
                        buildAlertDialog(movie);
                    }
                } else {
                    if (helperService.toDoWithFavourite((TVShow)movie)){
                        Snackbar.make(mRecyclerView,"Added to favourite",Snackbar.LENGTH_SHORT);
                    } else{
                        buildAlertDialog(movie);
                    }
                }
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }), new MyMovieListWrapper.OnEndReachListener() {
            @Override
            public void onEndReach(MainViewHolder mainViewHolder) {
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
            }
        }));

        Intent intent = getIntent();
        id = intent.getIntExtra(ID, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (onListOfMoviesGetListener == null) {
            onListOfMoviesGetListener = new OnListOfMoviesGetListener() {
                @Override
                public void onListOfMoviesGetListener(@NotNull List<? extends Movie> movies) {
                    if (movies.size() > 0) {
                        mPageViewModel.getList().addAll(movies);
                        mPageViewModel.setPage(mPageViewModel.getPage() + 1);
                    }
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            };
            movieService.addOnListOfMoviesGetListener(onListOfMoviesGetListener);
            Intent intent = getIntent();
            id = intent.getIntExtra(ID, -1);
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (onListOfMoviesGetListener != null) {
            movieService.removeOnListOfMoviesGetListener(onListOfMoviesGetListener);
            onListOfMoviesGetListener = null;
        }
    }

    private void buildAlertDialog(final TVEntity movie){
        new AlertDialog.Builder(SearchActivity.this)
                .setMessage(R.string.delete_from_watched)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbService.deleteFromDb(movie.getId());
                        Snackbar.make(mRecyclerView, "Deleted from favourite",
                                Snackbar.LENGTH_LONG).show();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbService.cancelFavourite(movie.getId());
                        Snackbar.make(mRecyclerView, "Deleted from favourite",
                                Snackbar.LENGTH_LONG).show();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                }).create()
                .show();
    }
}
