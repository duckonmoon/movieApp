package movies.test.softserve.movies.repository;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.constans.Constants;
import movies.test.softserve.movies.entity.TVPage;
import movies.test.softserve.movies.entity.TVShow;
import movies.test.softserve.movies.event.OnListOfTVShowsGetListener;
import movies.test.softserve.movies.service.TVShowsService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rkrit on 07.11.17.
 */

public class TVShowsRepository {

    private int page = 1;
    private List<TVShow> tvShows;


    private List<OnListOfTVShowsGetListener> listOfTVShowsGetListeners;
    private static TVShowsRepository INSTANCE;

    private TVShowsService service;


    private TVShowsRepository(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(TVShowsService.class);

        tvShows = new ArrayList<>();

        listOfTVShowsGetListeners = new ArrayList<>();
    }

    public static synchronized TVShowsRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (MoviesRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TVShowsRepository();
                }
            }
        }
        return INSTANCE;
    }


    public void tryToGetTVShows(){
        Call<TVPage> call = service.getTopRatedTVShows(Constants.API_KEY,page);
        call.enqueue(new Callback<TVPage>() {
            @Override
            public void onResponse(Call<TVPage> call, Response<TVPage> response) {
                if (response.body()!=null){
                    Log.w("Success",response.body().toString());
                    tvShows.addAll(response.body().getResults());
                    page++;
                    for (OnListOfTVShowsGetListener listener:
                         listOfTVShowsGetListeners) {
                        listener.onListOfTVShowsGet(response.body().getResults());
                    }
                }
            }

            @Override
            public void onFailure(Call<TVPage> call, Throwable t) {
                Log.e("Smth went wrong", t.getMessage());
            }
        });
    }


    public List<TVShow> getTvShows() {
        return tvShows;
    }

    public void addOnListOfTVShowsGetListener(OnListOfTVShowsGetListener listener){
        listOfTVShowsGetListeners.add(listener);
    }

    public void removeOnListOfTVShowsGetListener(OnListOfTVShowsGetListener listener){
        listOfTVShowsGetListeners.remove(listener);
    }
}
