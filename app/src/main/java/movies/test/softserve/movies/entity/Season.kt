package movies.test.softserve.movies.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Season {

    @SerializedName("air_date")
    @Expose
    var airDate: String? = null
    @SerializedName("episode_count")
    @Expose
    var episodeCount: Int? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("poster_path")
    @Expose
    var posterPath: String? = null
    @SerializedName("season_number")
    @Expose
    var seasonNumber: Int? = null

}