package movies.test.softserve.movies.service;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.constans.Constants;
import movies.test.softserve.movies.controller.MainController;
import movies.test.softserve.movies.entity.AppToken;
import movies.test.softserve.movies.entity.Code;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.GuestSession;
import movies.test.softserve.movies.entity.LoginSession;
import movies.test.softserve.movies.entity.Rating;
import movies.test.softserve.movies.event.OnAppTokenGetListener;
import movies.test.softserve.movies.event.OnInfoUpdatedListener;
import movies.test.softserve.movies.event.OnLoginSessionGetListener;
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
    private List<OnAppTokenGetListener> onAppTokenGetListeners;
    private List<OnLoginSessionGetListener> onLoginSessionGetListeners;


    private MovieService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MoviesService.class);
        listOfListeners = new ArrayList<>();
        onSessionGetListenersList = new ArrayList<>();
        onInfoUpdatedList = new ArrayList<>();
        onAppTokenGetListeners = new ArrayList<>();
        onLoginSessionGetListeners = new ArrayList<>();
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
        LoginSession loginSession = MainController.getInstance().getLoginSession();
        if (session != null) {
            if (loginSession==null) {
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
            }else {
                Call<Code> call = service.rateMovieLog(Constants.CONTENT_TYPE, movie_id, Constants.API_KEY, loginSession.getSessionId(), new Rating(value));
                Log.w("Hooray","i am here");
                call.enqueue(new Callback<Code>() {
                    @Override
                    public void onResponse(Call<Code> call, Response<Code> response) {
                        if (response.body()!=null) {
                            Log.d("Success", response.body().getStatusMessage());
                            for (OnInfoUpdatedListener listener :
                                    onInfoUpdatedList) {
                                listener.OnInfoUpdated(value / 2);
                            }
                        }else {
                            Log.w("i am here","Error occured");
                        }
                    }

                    @Override
                    public void onFailure(Call<Code> call, Throwable t) {
                        Log.e("Smth went wrong", t.getMessage());
                    }
                });
            }
        } else {
            tryToGetSession();
            Toast.makeText(MainController.getInstance().getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
        }
    }

    public void tryToGetToken() {
        Call<AppToken> call = service.getAppToken(Constants.API_KEY);
        call.enqueue(new Callback<AppToken>() {
            @Override
            public void onResponse(Call<AppToken> call, Response<AppToken> response) {
                Log.d("Success", "" + response.body().getSuccess());
                for (OnAppTokenGetListener listener :
                        onAppTokenGetListeners) {
                    listener.onAppTokenGet(response.body());
                }
            }

            @Override
            public void onFailure(Call<AppToken> call, Throwable t) {
                Log.e("Smth went wrong", t.getMessage());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tryToGetToken();
                    }
                }, 2000);

            }
        });
    }

    public void tryToGetLoginSession() {
        Call<LoginSession> call = service.getLoginSession(Constants.API_KEY, MainController.getInstance().getAppToken().getRequestToken());
        call.enqueue(new Callback<LoginSession>() {
            @Override
            public void onResponse(Call<LoginSession> call, Response<LoginSession> response) {
                if (response.body() != null) {
                    Log.d("Success", "" + response.body().getSuccess());
                    for (OnLoginSessionGetListener listener :
                            onLoginSessionGetListeners) {
                        listener.OnLoginSessionGet(response.body());
                    }
                }
            }
            @Override
            public void onFailure(Call<LoginSession> call, Throwable t) {
                Log.w("here", "Cant get login session");
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

    public void removeSessionListener(@NonNull OnSessionGetListener listener) {
        onSessionGetListenersList.remove(listener);
    }

    public void addOnInfoUpdatedListener(@NonNull OnInfoUpdatedListener listener) {
        onInfoUpdatedList.add(listener);
    }

    public void removeOnInfoUpdatedListener(@NonNull OnInfoUpdatedListener listener) {
        onInfoUpdatedList.remove(listener);
    }

    public void addOnAppTokenListener(@NonNull OnAppTokenGetListener listener) {
        onAppTokenGetListeners.add(listener);
    }

    public void removeOnAppTokenListener(@NonNull OnAppTokenGetListener listener) {
        onAppTokenGetListeners.remove(listener);
    }

    public void addOnLoginSessionGetListener(@NonNull OnLoginSessionGetListener listener){
        onLoginSessionGetListeners.add(listener);
    }

    public void removeOnLoginSessionGetListener(@NonNull OnLoginSessionGetListener listener){
        onLoginSessionGetListeners.remove(listener);
    }
}
