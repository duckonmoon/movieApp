package movies.test.softserve.movies.repository;


import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

import movies.test.softserve.movies.constans.Constants;
import movies.test.softserve.movies.controller.MainController;
import movies.test.softserve.movies.entity.Page;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.service.MoviesService;
import movies.test.softserve.movies.util.Mapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rkrit on 23.10.17.
 */

public class MoviesRepository extends Observable {
    private static MoviesRepository INSTANCE = null;

    private MoviesService service;
    private List<TVEntity> movieList;
    private int numberOfRequests;
    private String message;

    private MoviesRepository() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    try {
                        return chain.proceed(request);
                    } catch (Exception e) {
                        Log.e(Constants.ERROR, e.toString());
                        numberOfRequests++;
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        tryAgain(e);
                    }
                    throw new IOException();
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MoviesService.class);
    }

    public static synchronized MoviesRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (MoviesRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MoviesRepository();
                }
            }
        }
        return INSTANCE;
    }

    public synchronized void tryToGetAllMovies() {
        numberOfRequests = 0;
        Call<Page> call = service.getPage(Constants.API_KEY, MainController.getInstance().getPage(),
                Locale.getDefault().getLanguage());
        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(@NonNull Call<Page> call, @NonNull Response<Page> response) {
                try {
                    movieList = Mapper.mapFromMovieToTVEntity(response.body().getMovies());
                    MoviesRepository.this.setChanged();
                    MoviesRepository.this.notifyObservers();
                } catch (Exception e) {
                    tryToGetAllMovies();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Page> call, @NonNull Throwable t) {
                Log.e(Constants.ERROR, t.toString());

            }
        });
    }


    private synchronized void tryAgain(Exception e) {
        if (numberOfRequests < 3) {
            Call<Page> call = service.getPage(Constants.API_KEY, MainController.getInstance().getPage(),
                    Locale.getDefault().getLanguage());
            call.enqueue(new Callback<Page>() {
                @Override
                public void onResponse(@NonNull Call<Page> call, @NonNull Response<Page> response) {
                    try {
                        movieList = Mapper.mapFromMovieToTVEntity(response.body().getMovies());
                        MoviesRepository.this.setChanged();
                        MoviesRepository.this.notifyObservers();
                    } catch (Exception e) {
                        tryToGetAllMovies();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<Page> call, @NonNull Throwable t) {
                    Log.e(Constants.ERROR, t.toString());

                }
            });
        } else {
            movieList = null;
            message = e.getMessage();
            MoviesRepository.this.setChanged();
            MoviesRepository.this.notifyObservers();
        }
    }

    public String getMessage() {
        return message;
    }

    public List<TVEntity> getMovieList() {
        return movieList;
    }
}
