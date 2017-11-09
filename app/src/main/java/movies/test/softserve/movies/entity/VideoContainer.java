package movies.test.softserve.movies.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoContainer implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<Video> results = new ArrayList<Video>();
    public final static Parcelable.Creator<VideoContainer> CREATOR = new Creator<VideoContainer>() {


        @SuppressWarnings({
                "unchecked"
        })
        public VideoContainer createFromParcel(Parcel in) {
            return new VideoContainer(in);
        }

        public VideoContainer[] newArray(int size) {
            return (new VideoContainer[size]);
        }

    }
            ;
    private final static long serialVersionUID = -5544989223604801752L;

    protected VideoContainer(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.results, (movies.test.softserve.movies.entity.Video.class.getClassLoader()));
    }

    public VideoContainer() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(results);
    }

    public int describeContents() {
        return 0;
    }

}