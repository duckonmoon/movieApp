package movies.test.softserve.movies.repository;


import android.util.Log;

import java.util.List;
import java.util.Observable;

import movies.test.softserve.movies.constans.Constans;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.entity.Page;
import movies.test.softserve.movies.service.IMoviesService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rkrit on 23.10.17.
 */

public class MoviesRepository extends Observable{
    private IMoviesService service;
    private static MoviesRepository INSTANCE = null;
    private Integer page;
    private Boolean isBusy;
    private List<Movie> movieList;
    private MoviesRepository()
    {
        page = 1;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(IMoviesService.class);
        isBusy = false;
    }


    public static synchronized MoviesRepository getInstance() {
        if(INSTANCE == null) {
            synchronized (MoviesRepository.class) {
                if (INSTANCE==null) {
                    INSTANCE = new MoviesRepository();
                }
            }
        }
        return INSTANCE;
    }



    public synchronized void trytogetAllMovies()
    {
        synchronized (page) {
            Call<Page> call = service.getPage(Constans.API_KEY, page);
            call.enqueue(new Callback<Page>() {
                @Override
                public void onResponse(Call<Page> call, Response<Page> response) {
                    page = response.body().getPage() + 1;
                    movieList = response.body().getMovies();
                    MoviesRepository.this.setChanged();
                    MoviesRepository.this.notifyObservers();
                }

                @Override
                public void onFailure(Call<Page> call, Throwable t) {
                    Log.e("Smth went wrong", t.toString());
                }
            });
        }
    }

    public List<Movie> getMovieList(){
        return movieList;
    }
}
