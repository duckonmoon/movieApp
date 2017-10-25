package movies.test.softserve.movies.service;

import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.Page;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 8da77e727f0dbbe30d41e7ded59bb382
 * Created by rkrit on 20.10.17.
 */

public interface MoviesService {
    @GET("3/discover/movie")
    Call<Page> getPage(@Query("api_key") String apiKey, @Query("page") int page);

    @GET("3/movie/{movie_id}")
    Call<FullMovie> getMovie(@Path("movie_id") Integer movie_id, @Query("api_key") String apiKey);
}
