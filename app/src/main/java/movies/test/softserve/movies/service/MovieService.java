package movies.test.softserve.movies.service;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import movies.test.softserve.movies.constans.Constants;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.event.OnMovieInformationGet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rkrit on 25.10.17.
 */

public class MovieService {

    private static MovieService INSTANCE;
    private MoviesService service;
    private List<OnMovieInformationGet> listOfListeners;


    private MovieService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MoviesService.class);
        listOfListeners = new ArrayList<>();
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

    public synchronized void tryToGetMovie(Integer id) {
        Call<FullMovie> call = service.getMovie(id, Constants.API_KEY);
        call.enqueue(new Callback<FullMovie>() {
            @Override
            public void onResponse(Call<FullMovie> call, Response<FullMovie> response) {
                FullMovie fullMovie = response.body();
                for (OnMovieInformationGet listener :
                        listOfListeners) {
                    listener.onMovieGet(fullMovie);
                }
            }

            @Override
            public void onFailure(Call<FullMovie> call, Throwable t) {
                Log.e("Smth went wrong", t.getMessage());
            }
        });
    }


    public void addListener(@NonNull OnMovieInformationGet listener) {
        listOfListeners.add(listener);
    }

    public void removeListener(@NonNull OnMovieInformationGet listener){
        listOfListeners.remove(listener);
    }
}
