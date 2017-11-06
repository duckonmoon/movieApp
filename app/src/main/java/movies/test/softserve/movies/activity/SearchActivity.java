package movies.test.softserve.movies.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.jar.Attributes;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.MyMovieListWrapper;
import movies.test.softserve.movies.adapter.MyMovieRecyclerViewAdapter;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.event.OnListOfMoviesGetListener;
import movies.test.softserve.movies.service.MovieService;
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

    OnListOfMoviesGetListener onListOfMoviesGetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        try {
            setTitle(getIntent().getStringExtra(NAME));
        }catch (Exception ignored){}
        mPageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MyMovieListWrapper(new MyMovieRecyclerViewAdapter(mPageViewModel.getList(), new MyMovieRecyclerViewAdapter.OnMovieSelect() {
            @Override
            public void OnMovieSelected(Movie movie) {

            }
        }), new MyMovieListWrapper.OnEndReachListener() {
            @Override
            public void onEndReach() {
                switch (getIntent().getStringExtra(SEARCH_PARAM)) {
                    case GENRES:
                        MovieService.getInstance().getMovieByGenreCompany(id,null,mPageViewModel.getPage());
                        break;
                    case COMPANIES:
                        MovieService.getInstance().getMovieByGenreCompany(null,id,mPageViewModel.getPage());
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
                    mPageViewModel.getList().addAll(movies);
                    mPageViewModel.setPage(mPageViewModel.getPage()+1);
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            };
            MovieService.getInstance().addOnListOfMoviesGetListener(onListOfMoviesGetListener);
            Intent intent = getIntent();
            id = intent.getIntExtra(ID, -1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (onListOfMoviesGetListener != null) {
            MovieService.getInstance().removeOnListOfMoviesGetListener(onListOfMoviesGetListener);
        }
    }
}
