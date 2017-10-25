package movies.test.softserve.movies.repository;


import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Observable;

import movies.test.softserve.movies.constans.Constans;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.entity.Page;
import movies.test.softserve.movies.service.MoviesService;
import okhttp3.Interceptor;
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
    private Integer page;
    private List<Movie> movieList;
    private int numberOfRequests;
    private String message;


    private MoviesRepository() {
        page = 1;
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        try {
                            okhttp3.Response response = chain.proceed(request);
                            return response;
                        } catch (Exception e) {
                            Log.e("Smth went wrong", e.toString());
                            numberOfRequests++;
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            tryAgain(e);
                        }
                        throw new IOException();
                    }
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
        synchronized (page) {
            numberOfRequests = 0;
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

    private synchronized void tryAgain(Exception e) {
        if (numberOfRequests < 3) {
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

    public List<Movie> getMovieList() {
        return movieList;
    }
}
