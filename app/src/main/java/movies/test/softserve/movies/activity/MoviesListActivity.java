package movies.test.softserve.movies.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.MovieListAdapter;
import movies.test.softserve.movies.constans.Constans;
import movies.test.softserve.movies.entity.Page;
import movies.test.softserve.movies.entity.Result;
import movies.test.softserve.movies.service.MoviesService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MovieListAdapter mMovieListAdapter;
    private Page currentPage;
    private List<Result> resultList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);

        resultList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMovieListAdapter = new MovieListAdapter(resultList);
        mRecyclerView.setAdapter(mMovieListAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MoviesService service = retrofit.create(MoviesService.class);
        Call<Page> call = service.getPage(Constans.API_KEY,1);
        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {
                currentPage = response.body();
                resultList.addAll(currentPage.getResults());
                mMovieListAdapter.notifyDataSetChanged();
                Log.d("Retrofit","Ok");
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                Log.e("Smth went wrong", t.toString());
            }
        });



    }
}
