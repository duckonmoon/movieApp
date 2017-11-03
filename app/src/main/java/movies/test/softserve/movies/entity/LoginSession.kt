package movies.test.softserve.movies.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginSession {

    @SerializedName("success")
    @Expose
    var success: Boolean? = null
    @SerializedName("session_id")
    @Expose
    var sessionId: String? = null

}