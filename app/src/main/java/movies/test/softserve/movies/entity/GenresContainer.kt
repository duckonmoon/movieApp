package movies.test.softserve.movies.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class GenresContainer {
    @SerializedName("genres")
    @Expose
    var genres: List<Genre> = ArrayList()
}