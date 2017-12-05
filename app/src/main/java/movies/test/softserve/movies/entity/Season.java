package movies.test.softserve.movies.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Season implements Serializable, Parcelable {

    public final static Parcelable.Creator<Season> CREATOR = new Creator<Season>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Season createFromParcel(Parcel in) {
            return new Season(in);
        }

        public Season[] newArray(int size) {
            return (new Season[size]);
        }

    };
    private final static long serialVersionUID = -1002491789578755083L;
    @SerializedName("air_date")
    @Expose
    private String airDate;
    @SerializedName("episode_count")
    @Expose
    private Integer episodeCount;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("season_number")
    @Expose
    private Integer seasonNumber;

    protected Season(Parcel in) {
        this.airDate = ((String) in.readValue((String.class.getClassLoader())));
        this.episodeCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.posterPath = ((String) in.readValue((String.class.getClassLoader())));
        this.seasonNumber = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public Season() {
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public Integer getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(Integer episodeCount) {
        this.episodeCount = episodeCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(airDate);
        dest.writeValue(episodeCount);
        dest.writeValue(id);
        dest.writeValue(posterPath);
        dest.writeValue(seasonNumber);
    }

    public int describeContents() {
        return 0;
    }

}