package movies.test.softserve.movies.service

import android.net.Uri
import movies.test.softserve.movies.entity.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by rkrit on 07.11.17.
 */
interface TVShowsService {
    @GET("3/tv/top_rated")
    fun getTopRatedTVShows(@Query("api_key") apiKey: String,
                           @Query("page") page: Int?,
                           @Query("language") language: String): Call<TVPage>

    @GET("3/tv/{tv_id}")
    fun getTVShow(@Path("tv_id") tvID: Int,
                  @Query("api_key") apiKey: String): Call<FullTVShow>

    @POST("3/tv/{tv_id}/rating")
    fun rateTVShow(@Header("Content-Type") content_type: String,
                   @Path("tv_id") tvID: Int,
                   @Query("api_key") apiKey: String,
                   @Query("guest_session_id") guestSessionId: String,
                   @Body value: Rating): Call<Code>

    @GET("3/tv/{tv_id}/season/{season_number}/videos")
    fun getVideos(@Path("tv_id") tvID: Int,
                  @Path("season_number") season_number: Int,
                  @Query("api_key") apiKey: String): Call<VideoContainer>

    @GET("3/search/tv")
    fun getTVShowByKeyword(@Query("api_key") apiKey: String,
                           @Query("query") uri: Uri,
                           @Query("page") page: Int,
                           @Query("language") language: String): Call<TVPage>

    @GET("3/tv/{tv_id}/similar")
    fun getSimilarTVShows(@Path("tv_id") tv_id: Int,
                          @Query("api_key") apiKey: String,
                          @Query("language") language: String,
                          @Query("page") page: Int?): Call<TVPage>
}