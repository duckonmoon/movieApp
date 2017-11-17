package movies.test.softserve.movies.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class TVShow : TVEntity {

    @SerializedName("original_name")
    @Expose
    var originalName: String? = null
    @SerializedName("genre_ids")
    @Expose
    override var genreIds: List<Int>? = ArrayList()
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("popularity")
    @Expose
    var popularity: Double? = null
    @SerializedName("origin_country")
    @Expose
    var originCountry: List<String>? = ArrayList()
    @SerializedName("vote_count")
    @Expose
    override var voteCount: Int? = null
    @SerializedName("first_air_date")
    @Expose
    var firstAirDate: String? = null
    @SerializedName("backdrop_path")
    @Expose
    var backdropPath: String? = null
    @SerializedName("original_language")
    @Expose
    var originalLanguage: String? = null
    @SerializedName("id")
    @Expose
    override var id: Int? = null
    @SerializedName("vote_average")
    @Expose
    override var voteAverage: Double? = null
    @SerializedName("overview")
    @Expose
    override var overview: String? = null
    @SerializedName("poster_path")
    @Expose
    override var posterPath: String? = null

    override val title: String?
        get() = name
}