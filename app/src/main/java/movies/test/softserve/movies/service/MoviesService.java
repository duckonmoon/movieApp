package movies.test.softserve.movies.service;

import movies.test.softserve.movies.entity.AppToken;
import movies.test.softserve.movies.entity.Code;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.GuestSession;
import movies.test.softserve.movies.entity.LoginSession;
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

    @POST("3/movie/{movie_id}/rating")
    Call<Code> rateMovieLog(@Header("Content-Type") String contentType,@Path("movie_id") Integer movie_id, @Query("api_key") String apiKey, @Query("session_id") String sessionId, @Body Rating value);

    @GET("3/authentication/token/new")
    Call<AppToken> getAppToken(@Query("api_key") String apiKey);

    @GET("3/authentication/session/new")
    Call<LoginSession> getLoginSession(@Query("api_key")  String apiKey, @Query("request_token") String requestToken);
}
