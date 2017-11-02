package movies.test.softserve.movies.service;

import movies.test.softserve.movies.entity.Code;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.GuestSession;
import movies.test.softserve.movies.entity.Page;
import movies.test.softserve.movies.entity.Rating;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rkrit on 20.10.17.
 */

public interface MoviesService {
    @GET("3/discover/movie")
    Call<Page> getPage(@Query("api_key") String apiKey, @Query("page") int page);

    @GET("3/movie/{movie_id}")
    Call<FullMovie> getMovie(@Path("movie_id") Integer movie_id, @Query("api_key") String apiKey);

    @GET("3/authentication/guest_session/new")
    Call<GuestSession> getGuestSession(@Query("api_key") String apiKey);

    @POST("3/movie/{movie_id}/rating")
    Call<Code> rateMovie(@Header("Content-Type") String contentType,@Path("movie_id") Integer movie_id, @Query("api_key") String apiKey, @Query("guest_session_id") String guestSessionId, @Body Rating value);
}
