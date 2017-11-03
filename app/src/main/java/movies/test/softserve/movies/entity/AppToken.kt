package movies.test.softserve.movies.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AppToken {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null
    @SerializedName("expires_at")
    @Expose
    var expiresAt: String? = null
    @SerializedName("request_token")
    @Expose
    var requestToken: String? = null
}