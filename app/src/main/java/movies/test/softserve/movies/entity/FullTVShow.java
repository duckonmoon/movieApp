package movies.test.softserve.movies.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FullTVShow implements Serializable, Parcelable {

    public final static Parcelable.Creator<FullTVShow> CREATOR = new Creator<FullTVShow>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FullTVShow createFromParcel(Parcel in) {
            return new FullTVShow(in);
        }

        public FullTVShow[] newArray(int size) {
            return (new FullTVShow[size]);
        }

    };
    private final static long serialVersionUID = 8403401438291775201L;
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("created_by")
    @Expose
    private List<CreatedBy> createdBy = new ArrayList<>();
    @SerializedName("episode_run_time")
    @Expose
    private List<Integer> episodeRunTime = new ArrayList<>();
    @SerializedName("first_air_date")
    @Expose
    private String firstAirDate;
    @SerializedName("genres")
    @Expose
    private List<Genre> genres = new ArrayList<>();
    @SerializedName("homepage")
    @Expose
    private String homepage;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("in_production")
    @Expose
    private Boolean inProduction;
    @SerializedName("languages")
    @Expose
    private List<String> languages = new ArrayList<>();
    @SerializedName("last_air_date")
    @Expose
    private String lastAirDate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("networks")
    @Expose
    private List<Network> networks = new ArrayList<>();
    @SerializedName("number_of_episodes")
    @Expose
    private Integer numberOfEpisodes;
    @SerializedName("number_of_seasons")
    @Expose
    private Integer numberOfSeasons;
    @SerializedName("origin_country")
    @Expose
    private List<String> originCountry = new ArrayList<>();
    @SerializedName("original_language")
    @Expose
    private String originalLanguage;
    @SerializedName("original_name")
    @Expose
    private String originalName;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("popularity")
    @Expose
    private Double popularity;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("production_companies")
    @Expose
    private List<ProductionCompany> productionCompanies = new ArrayList<>();
    @SerializedName("seasons")
    @Expose
    private List<Season> seasons = new ArrayList<>();
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    protected FullTVShow(Parcel in) {
        this.backdropPath = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.createdBy, (movies.test.softserve.movies.entity.CreatedBy.class.getClassLoader()));
        in.readList(this.episodeRunTime, (java.lang.Integer.class.getClassLoader()));
        this.firstAirDate = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.genres, (movies.test.softserve.movies.entity.Genre.class.getClassLoader()));
        this.homepage = ((String) in.readValue((String.class.getClassLoader())));
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.inProduction = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        in.readList(this.languages, (java.lang.String.class.getClassLoader()));
        this.lastAirDate = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.networks, (movies.test.softserve.movies.entity.Network.class.getClassLoader()));
        this.numberOfEpisodes = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.numberOfSeasons = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.originCountry, (java.lang.String.class.getClassLoader()));
        this.originalLanguage = ((String) in.readValue((String.class.getClassLoader())));
        this.originalName = ((String) in.readValue((String.class.getClassLoader())));
        this.overview = ((String) in.readValue((String.class.getClassLoader())));
        this.popularity = ((Double) in.readValue((Double.class.getClassLoader())));
        this.posterPath = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.productionCompanies, (movies.test.softserve.movies.entity.ProductionCompany.class.getClassLoader()));
        in.readList(this.seasons, (movies.test.softserve.movies.entity.Season.class.getClassLoader()));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.voteAverage = ((Double) in.readValue((Double.class.getClassLoader())));
        this.voteCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public FullTVShow() {
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public List<CreatedBy> getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(List<CreatedBy> createdBy) {
        this.createdBy = createdBy;
    }

    public List<Integer> getEpisodeRunTime() {
        return episodeRunTime;
    }

    public void setEpisodeRunTime(List<Integer> episodeRunTime) {
        this.episodeRunTime = episodeRunTime;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getInProduction() {
        return inProduction;
    }

    public void setInProduction(Boolean inProduction) {
        this.inProduction = inProduction;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(List<Network> networks) {
        this.networks = networks;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public List<String> getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(List<String> originCountry) {
        this.originCountry = originCountry;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(backdropPath);
        dest.writeList(createdBy);
        dest.writeList(episodeRunTime);
        dest.writeValue(firstAirDate);
        dest.writeList(genres);
        dest.writeValue(homepage);
        dest.writeValue(id);
        dest.writeValue(inProduction);
        dest.writeList(languages);
        dest.writeValue(lastAirDate);
        dest.writeValue(name);
        dest.writeList(networks);
        dest.writeValue(numberOfEpisodes);
        dest.writeValue(numberOfSeasons);
        dest.writeList(originCountry);
        dest.writeValue(originalLanguage);
        dest.writeValue(originalName);
        dest.writeValue(overview);
        dest.writeValue(popularity);
        dest.writeValue(posterPath);
        dest.writeList(productionCompanies);
        dest.writeList(seasons);
        dest.writeValue(status);
        dest.writeValue(type);
        dest.writeValue(voteAverage);
        dest.writeValue(voteCount);
    }

    public int describeContents() {
        return 0;
    }

}