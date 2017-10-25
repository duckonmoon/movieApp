package movies.test.softserve.movies.service;

import android.util.Log;

import java.util.Observable;

import movies.test.softserve.movies.constans.Constans;
import movies.test.softserve.movies.entity.FullMovie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rkrit on 25.10.17.
 */

public class MovieService extends Observable {

    private static MovieService INSTANCE;
    private MoviesService service;
    private FullMovie fullMovie;

    private MovieService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MoviesService.class);
    }


    public static synchronized MovieService getInstance() {
        if (INSTANCE == null) {
            synchronized (MovieService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MovieService();
                }
            }
        }
        return INSTANCE;
    }

    public synchronized void tryToGetMovie(Integer id){
        Call<FullMovie> call = service.getMovie(id, Constans.API_KEY);
        call.enqueue(new Callback<FullMovie>() {
            @Override
            public void onResponse(Call<FullMovie> call, Response<FullMovie> response) {
                fullMovie = response.body();
                MovieService.this.setChanged();
                MovieService.this.notifyObservers();
            }

            @Override
            public void onFailure(Call<FullMovie> call, Throwable t) {
                Log.e("Smth went wrong", t.getMessage());
            }
        });
    }

    public FullMovie getFullMovie() {
        return fullMovie;
    }
}
