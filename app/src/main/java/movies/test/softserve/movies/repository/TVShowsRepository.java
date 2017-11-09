package movies.test.softserve.movies.repository;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.constans.Constants;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.FullTVShow;
import movies.test.softserve.movies.entity.TVPage;
import movies.test.softserve.movies.entity.TVShow;
import movies.test.softserve.movies.event.OnFullTVShowGetListener;
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

    private static TVShowsRepository INSTANCE;

    private int page = 1;
    private List<TVShow> tvShows;

    private TVShowsService service;

    private List<OnListOfTVShowsGetListener> listOfTVShowsGetListeners;
    private List<OnFullTVShowGetListener> onFullTVShowGetListeners;

    private TVShowsRepository(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(TVShowsService.class);

        tvShows = new ArrayList<>();

        listOfTVShowsGetListeners = new ArrayList<>();
        onFullTVShowGetListeners = new ArrayList<>();
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

    public void trytoGetFullTVShow(Integer id){
        Call<FullTVShow> call = service.getTVShow(id,Constants.API_KEY);
        call.enqueue(new Callback<FullTVShow>() {
            @Override
            public void onResponse(Call<FullTVShow> call, Response<FullTVShow> response) {
                Log.w("Success",response.body().toString());
                if (response.body()!=null){
                    for (OnFullTVShowGetListener listener:
                         onFullTVShowGetListeners) {
                        listener.onFullTVShowGet(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<FullTVShow> call, Throwable t) {
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

    public void addOnFullTVShowGetListeners(OnFullTVShowGetListener listener){
        onFullTVShowGetListeners.add(listener);
    }

    public void removeOnFullTVShowGetListeners(OnFullTVShowGetListener listener){
        onFullTVShowGetListeners.remove(listener);
    }
}
