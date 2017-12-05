package movies.test.softserve.movies.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GuestSession implements Parcelable {

    public final static Parcelable.Creator<GuestSession> CREATOR = new Creator<GuestSession>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GuestSession createFromParcel(Parcel in) {
            return new GuestSession(in);
        }

        public GuestSession[] newArray(int size) {
            return (new GuestSession[size]);
        }

    };
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("guest_session_id")
    @Expose
    private String guestSessionId;
    @SerializedName("expires_at")
    @Expose
    private String expiresAt;

    protected GuestSession(Parcel in) {
        this.success = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.guestSessionId = ((String) in.readValue((String.class.getClassLoader())));
        this.expiresAt = ((String) in.readValue((String.class.getClassLoader())));
    }

    public GuestSession() {
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getGuestSessionId() {
        return guestSessionId;
    }

    public void setGuestSessionId(String guestSessionId) {
        this.guestSessionId = guestSessionId;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(success);
        dest.writeValue(guestSessionId);
        dest.writeValue(expiresAt);
    }

    public int describeContents() {
        return 0;
    }

}