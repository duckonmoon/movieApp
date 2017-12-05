package movies.test.softserve.movies.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Network implements Serializable, Parcelable {

    public final static Parcelable.Creator<Network> CREATOR = new Creator<Network>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Network createFromParcel(Parcel in) {
            return new Network(in);
        }

        public Network[] newArray(int size) {
            return (new Network[size]);
        }

    };
    private final static long serialVersionUID = 7676287473115122540L;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;

    protected Network(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Network() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
    }

    public int describeContents() {
        return 0;
    }

}