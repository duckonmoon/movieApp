package movies.test.softserve.movies.service

import movies.test.softserve.movies.entity.Code
import movies.test.softserve.movies.entity.FullTVShow
import movies.test.softserve.movies.entity.Rating
import movies.test.softserve.movies.entity.TVPage
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by rkrit on 07.11.17.
 */
interface TVShowsService {
    @GET("3/tv/top_rated")
    fun getTopRatedTVShows(@Query("api_key") apiKey: String, @Query("page") page: Int?): Call<TVPage>

    @GET("3/tv/{tv_id}")
    fun getTVShow(@Path("tv_id") tvID: Int, @Query("api_key") apiKey: String): Call<FullTVShow>

    @POST("3/tv/{tv_id}/rating")
    fun rateTVShow(@Header("Content-Type") content_type: String,
                   @Path("tv_id") tvID: Int,
                   @Query("api_key") apiKey: String,
                   @Query("guest_session_id") guestSessionId: String,
                   @Body value: Rating) : Call<Code>
}