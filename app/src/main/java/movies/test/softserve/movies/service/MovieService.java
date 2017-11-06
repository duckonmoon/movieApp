package movies.test.softserve.movies.service;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.constans.Constants;
import movies.test.softserve.movies.controller.MainController;
import movies.test.softserve.movies.entity.Code;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.GuestSession;
import movies.test.softserve.movies.entity.Page;
import movies.test.softserve.movies.entity.Rating;
import movies.test.softserve.movies.event.OnInfoUpdatedListener;
import movies.test.softserve.movies.event.OnListOfMoviesGetListener;
import movies.test.softserve.movies.event.OnMovieInformationGet;
import movies.test.softserve.movies.event.OnSessionGetListener;
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
    private List<OnSessionGetListener> onSessionGetListenersList;
    private List<OnInfoUpdatedListener> onInfoUpdatedList;
    private List<OnListOfMoviesGetListener> onListOfMoviesGetListeners;

    private MovieService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MoviesService.class);
        listOfListeners = new ArrayList<>();
        onSessionGetListenersList = new ArrayList<>();
        onInfoUpdatedList = new ArrayList<>();
        onListOfMoviesGetListeners = new ArrayList<>();
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

    public synchronized void tryToGetSession() {
        Call<GuestSession> call = service.getGuestSession(Constants.API_KEY);
        call.enqueue(new Callback<GuestSession>() {
            @Override
            public void onResponse(Call<GuestSession> call, Response<GuestSession> response) {
                GuestSession guestSession = response.body();
                for (OnSessionGetListener listener :
                        onSessionGetListenersList) {
                    listener.onSessionGet(guestSession);
                }
            }

            @Override
            public void onFailure(Call<GuestSession> call, Throwable t) {
                Log.e("Smth went wrong", t.getMessage());
            }
        });

    }

    public void rateMovie(Integer movie_id, final float value) {
        GuestSession session = MainController.getInstance().getGuestSession();
        if (session != null) {
            Call<Code> call = service.rateMovie(Constants.CONTENT_TYPE, movie_id, Constants.API_KEY, session.getGuestSessionId(), new Rating(value));
            call.enqueue(new Callback<Code>() {
                @Override
                public void onResponse(Call<Code> call, Response<Code> response) {
                    Log.d("Success", response.body().getStatusMessage());
                    for (OnInfoUpdatedListener listener :
                            onInfoUpdatedList) {
                        listener.OnInfoUpdated(value / 2);
                    }
                }

                @Override
                public void onFailure(Call<Code> call, Throwable t) {
                    Log.e("Smth went wrong", t.getMessage());
                }
            });
        } else {
            tryToGetSession();
            Toast.makeText(MainController.getInstance().getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
        }
    }


    public void getMovieByGenreCompany(Integer genre, Integer productionCompany, Integer page){
        Call<Page> call = service.discoverMovie(Constants.API_KEY,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                page,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                productionCompany,
                genre,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
                );
        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {
                for (OnListOfMoviesGetListener listener:
                     onListOfMoviesGetListeners) {
                    listener.onListOfMoviesGetListener(response.body().getMovies());
                }
                Log.w("i am here" , "" + response.body());
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                Log.e("Smth went wrong", t.getMessage());
            }
        });

    }


    public void addListener(@NonNull OnMovieInformationGet listener) {
        listOfListeners.add(listener);
    }

    public void removeListener(@NonNull OnMovieInformationGet listener) {
        listOfListeners.remove(listener);
    }

    public void addSessionListener(@NonNull OnSessionGetListener listener) {
        onSessionGetListenersList.add(listener);
    }


    public void addOnInfoUpdatedListener(@NonNull OnInfoUpdatedListener listener) {
        onInfoUpdatedList.add(listener);
    }

    public void removeOnInfoUpdatedListener(@NonNull OnInfoUpdatedListener listener) {
        onInfoUpdatedList.remove(listener);
    }

    public void addOnListOfMoviesGetListener(@NonNull OnListOfMoviesGetListener listener){
        onListOfMoviesGetListeners.add(listener);
    }

    public void removeOnListOfMoviesGetListener(@NonNull OnListOfMoviesGetListener listener){
        onListOfMoviesGetListeners.remove(listener);
    }

}
