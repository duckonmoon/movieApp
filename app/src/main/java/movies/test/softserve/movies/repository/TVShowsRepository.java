package movies.test.softserve.movies.repository;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import movies.test.softserve.movies.constans.Constants;
import movies.test.softserve.movies.controller.MainController;
import movies.test.softserve.movies.db.entity.MovieFirebaseDTO;
import movies.test.softserve.movies.entity.Code;
import movies.test.softserve.movies.entity.FullTVShow;
import movies.test.softserve.movies.entity.GuestSession;
import movies.test.softserve.movies.entity.Rating;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.entity.TVPage;
import movies.test.softserve.movies.entity.VideoContainer;
import movies.test.softserve.movies.event.OnFullTVShowGetListener;
import movies.test.softserve.movies.event.OnFullTVShowInformationGetListener;
import movies.test.softserve.movies.event.OnInfoUpdatedListener;
import movies.test.softserve.movies.event.OnListOfTVShowsGetListener;
import movies.test.softserve.movies.event.OnSimilarTVEntitiesGetListener;
import movies.test.softserve.movies.service.MovieService;
import movies.test.softserve.movies.service.TVShowsService;
import movies.test.softserve.movies.util.Mapper;
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
    private List<TVEntity> tvShows;

    private TVShowsService service;

    private List<OnListOfTVShowsGetListener> listOfTVShowsGetListeners;
    private List<OnFullTVShowGetListener> onFullTVShowGetListeners;
    private List<OnInfoUpdatedListener> onInfoUpdatedList;
    private List<OnSimilarTVEntitiesGetListener> onSimilarTVEntitiesGetListeners;
    private OnFullTVShowInformationGetListener onFullTVShowInformationGetListener;


    private TVShowsRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(TVShowsService.class);

        tvShows = new ArrayList<>();

        onFullTVShowInformationGetListener = null;
        listOfTVShowsGetListeners = new ArrayList<>();
        onFullTVShowGetListeners = new ArrayList<>();
        onInfoUpdatedList = new ArrayList<>();
        onSimilarTVEntitiesGetListeners = new ArrayList<>();
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


    public void tryToGetTVShows() {
        Call<TVPage> call = service.getTopRatedTVShows(Constants.API_KEY, page, Locale.getDefault().getLanguage());
        call.enqueue(new Callback<TVPage>() {
            @Override
            public void onResponse(Call<TVPage> call, Response<TVPage> response) {
                if (response.body() != null) {
                    Log.w("Success", response.body().toString());
                    tvShows.addAll(Mapper.mapFromTVShowToTVEntity(response.body().getResults()));
                    page++;
                    for (OnListOfTVShowsGetListener listener :
                            listOfTVShowsGetListeners) {
                        listener.onListOfTVShowsGet();
                    }
                }
            }

            @Override
            public void onFailure(Call<TVPage> call, Throwable t) {
                Log.e("Smth went wrong", t.getMessage());
            }
        });
    }

    public void trytoGetFullTVShow(Integer id) {
        Call<FullTVShow> call = service.getTVShow(id, Constants.API_KEY);
        call.enqueue(new Callback<FullTVShow>() {
            @Override
            public void onResponse(Call<FullTVShow> call, Response<FullTVShow> response) {
                if (response.body() != null) {
                    for (OnFullTVShowGetListener listener :
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

    public void trytoGetFullTVShow(MovieFirebaseDTO movieFirebaseDTO) {
        Call<FullTVShow> call = service.getTVShow(movieFirebaseDTO.getId(), Constants.API_KEY);
        call.enqueue(new Callback<FullTVShow>() {
            @Override
            public void onResponse(Call<FullTVShow> call, Response<FullTVShow> response) {
                if (onFullTVShowInformationGetListener != null && response.body()!=null) {
                    onFullTVShowInformationGetListener.onFullTVShowGet(response.body(), movieFirebaseDTO);
                } else {
                    Log.e("Error loading",movieFirebaseDTO.getId().toString());
                    trytoGetFullTVShow(movieFirebaseDTO);
                }
            }

            @Override
            public void onFailure(Call<FullTVShow> call, Throwable t) {
                Log.e("Smth went wrong", t.getMessage());
                trytoGetFullTVShow(movieFirebaseDTO);
            }
        });
    }


    public void rateTVShow(Integer tvShow_id, final float value) {
        GuestSession session = MainController.getInstance().getGuestSession();
        if (session != null) {
            Call<Code> call = service.rateTVShow(Constants.CONTENT_TYPE, tvShow_id, Constants.API_KEY, session.getGuestSessionId(), new Rating(value));
            call.enqueue(new Callback<Code>() {
                @Override
                public void onResponse(Call<Code> call, Response<Code> response) {
                    Log.d("Success", response.body().getStatusMessage());
                    for (OnInfoUpdatedListener listener
                            : onInfoUpdatedList) {
                        listener.OnInfoUpdated(value / 2);
                    }
                }

                @Override
                public void onFailure(Call<Code> call, Throwable t) {
                    Log.e("Smth went wrong", t.getMessage());
                }
            });
        } else {
            MovieService.getInstance().tryToGetSession();
            Toast.makeText(MainController.getInstance().getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
        }
    }

    public void getVideo(Integer tv_id, Integer season_number) {
        Call<VideoContainer> call = service.getVideos(tv_id, season_number, Constants.API_KEY);
        call.enqueue(new Callback<VideoContainer>() {
            @Override
            public void onResponse(Call<VideoContainer> call, Response<VideoContainer> response) {
                Log.d("Success", "" + response.body().getResults());
            }

            @Override
            public void onFailure(Call<VideoContainer> call, Throwable t) {
                Log.e("Smth went wrong", t.getMessage());
            }
        });
    }

    public void tryToGetSimilarTvShows(Integer tv_id, Integer page) {
        Call<TVPage> call = service.getSimilarTVShows(tv_id, Constants.API_KEY, Locale.getDefault().getLanguage(), page);
        call.enqueue(new Callback<TVPage>() {
            @Override
            public void onResponse(Call<TVPage> call, Response<TVPage> response) {
                if (response.body() != null) {
                    for (OnSimilarTVEntitiesGetListener listener :
                            onSimilarTVEntitiesGetListeners) {
                        listener.onSimilarTVEntitiesGetListener(Mapper.mapFromTVShowToTVEntity(response.body().getResults()));
                    }
                }
            }

            @Override
            public void onFailure(Call<TVPage> call, Throwable t) {
                Log.e("Smth went wrong", t.getMessage());
            }
        });
    }

    public void getTVShowByKeyword(@NonNull String query, @NonNull Integer page, @NonNull Callback<TVPage> callback) {
        Call<TVPage> call = service.getTVShowByKeyword(Constants.API_KEY, Uri.parse(query.trim()), page, Locale.getDefault().getLanguage());
        call.enqueue(callback);
    }


    public List<TVEntity> getTvShows() {
        return tvShows;
    }

    public void addOnListOfTVShowsGetListener(OnListOfTVShowsGetListener listener) {
        listOfTVShowsGetListeners.add(listener);
    }

    public void removeOnListOfTVShowsGetListener(OnListOfTVShowsGetListener listener) {
        listOfTVShowsGetListeners.remove(listener);
    }

    public void addOnFullTVShowGetListeners(OnFullTVShowGetListener listener) {
        onFullTVShowGetListeners.add(listener);
    }

    public void removeOnFullTVShowGetListeners(OnFullTVShowGetListener listener) {
        onFullTVShowGetListeners.remove(listener);
    }

    public void addOnFullTVShowGetListeners(OnFullTVShowInformationGetListener listener) {
        onFullTVShowInformationGetListener = listener;
    }

    public void removeOnFullTVShowGetListeners() {
        onFullTVShowInformationGetListener = null;
    }

    public void addOnInfoUpdatedListener(@NonNull OnInfoUpdatedListener listener) {
        onInfoUpdatedList.add(listener);
    }

    public void removeOnInfoUpdatedListener(@NonNull OnInfoUpdatedListener listener) {
        onInfoUpdatedList.remove(listener);
    }

    public void addOnSimilarTVEntitiesGetListener(OnSimilarTVEntitiesGetListener listener) {
        onSimilarTVEntitiesGetListeners.add(listener);
    }

    public void removeOnSimilarTVEntitiesGetListener(OnSimilarTVEntitiesGetListener listener) {
        onSimilarTVEntitiesGetListeners.remove(listener);
    }
}
