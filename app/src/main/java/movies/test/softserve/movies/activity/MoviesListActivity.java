package movies.test.softserve.movies.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.MovieListAdapter;

public class MoviesListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MovieListAdapter mMovieListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMovieListAdapter = MovieListAdapter.getInstance(this);
        mRecyclerView.setAdapter(mMovieListAdapter);
    }
}
