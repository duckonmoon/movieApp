package movies.test.softserve.movies.service

import movies.test.softserve.movies.entity.TVPage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by rkrit on 07.11.17.
 */
interface TVShowsService {
    @GET("3/tv/top_rated")
    fun getTopRatedTVShows(@Query("api_key") apiKey: String,@Query("page") page : Int): Call<TVPage>
}