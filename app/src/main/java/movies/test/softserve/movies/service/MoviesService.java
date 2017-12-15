package movies.test.softserve.movies.service;

import android.net.Uri;

import movies.test.softserve.movies.entity.Code;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.GenresContainer;
import movies.test.softserve.movies.entity.GuestSession;
import movies.test.softserve.movies.entity.Page;
import movies.test.softserve.movies.entity.PosterContainer;
import movies.test.softserve.movies.entity.Rating;
import movies.test.softserve.movies.entity.VideoContainer;
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
    Call<Page> getPage(@Query("api_key") String apiKey,
                       @Query("page") int page,
                       @Query("language") String language);

    @GET("3/movie/{movie_id}")
    Call<FullMovie> getMovie(@Path("movie_id") Integer movie_id,
                             @Query("api_key") String apiKey,
                             @Query("language") String language);

    @GET("3/authentication/guest_session/new")
    Call<GuestSession> getGuestSession(@Query("api_key") String apiKey);

    @POST("3/movie/{movie_id}/rating")
    Call<Code> rateMovie(@Header("Content-Type") String contentType,
                         @Path("movie_id") Integer movie_id,
                         @Query("api_key") String apiKey,
                         @Query("guest_session_id") String guestSessionId,
                         @Body Rating value);

    @GET("3/discover/movie")
    Call<Page> discoverMovie(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("region") String region,
            @Query("sort_by") String sort_by,
            @Query("certification_country") String certification_country,
            @Query("certification.lte") String certification_lte,
            @Query("include_adult") Boolean include_adult,
            @Query("include_video") Boolean include_video,
            @Query("page") Integer page,
            @Query("primary_release_year") Integer primary_release_year,
            @Query("primary_release_date.gte") String primary_release_date_gte,
            @Query("primary_release_date.lte") String primary_release_date_lte,
            @Query("release_date.gte") String release_date_gte,
            @Query("release_date.lte") String release_date_lte,
            @Query("vote_count.gte") Integer vote_count_gte,
            @Query("vote_count.lte") Integer vote_count_lte,
            @Query("vote_average.gte") Double vote_average_gte,
            @Query("vote_average.lte") Double vote_average_lte,
            @Query("with_cast") String with_cast,
            @Query("with_crew") String with_crew,
            @Query("with_companies") Integer with_companies,
            @Query("with_genres") Integer with_genres,
            @Query("with_keywords") String with_keywords,
            @Query("with_people") String with_people,
            @Query("year") Integer year,
            @Query("without_genres") String without_genres,
            @Query("with_runtime.gte") Integer with_runtime_gte,
            @Query("with_runtime.lte") Integer with_runtime_lte,
            @Query("with_release_type") Integer with_release_type,
            @Query("with_original_language") String with_original_language,
            @Query("without_keywords") String without_keywords);

    @GET("3/genre/movie/list")
    Call<GenresContainer> getAllGenres(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("3/search/movie")
    Call<Page> getMovieByKeyword(@Query("api_key") String apiKey, @Query("query") Uri uri, @Query("page") Integer page, @Query("language") String language);

    @GET("3/movie/{movie_id}/videos")
    Call<VideoContainer> getVideosForMovie(@Path("movie_id") Integer movie_id, @Query("api_key") String apiKey, @Query("language") String language);

    @GET("3/movie/{movie_id}/images")
    Call<PosterContainer> getMoviePosters(@Path("movie_id") Integer movie_id,
                                          @Query("api_key") String apiKey,
                                          @Query("language") String language);

    @GET("3/movie/{movie_id}/similar")
    Call<Page> getSimilarMovies(@Path("movie_id") Integer movie_id,
                                @Query("api_key") String apiKey,
                                @Query("language") String language,
                                @Query("page") Integer page);

}
