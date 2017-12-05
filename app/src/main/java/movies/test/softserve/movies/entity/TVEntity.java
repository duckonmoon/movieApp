package movies.test.softserve.movies.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rkrit on 22.11.17.
 */

public class TVEntity implements Serializable {
    private Integer id;
    private String posterPath;
    private Double voteAverage;
    private Integer voteCount;
    private String title;
    private String overview;
    private String releaseDate = "";
    private List<Integer> genreIds = new ArrayList<>();
    private TYPE type;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public enum TYPE {
        TV_SHOW,
        MOVIE
    }

    public static class TVEntityBuilder {
        private TVEntity tvEntity = new TVEntity();

        public TVEntityBuilder setId(Integer id) {
            tvEntity.setId(id);
            return this;
        }

        public TVEntityBuilder setPosterPath(String posterPath) {
            tvEntity.posterPath = posterPath;
            return this;
        }

        public TVEntityBuilder setVoteAverage(Double voteAverage) {
            tvEntity.voteAverage = voteAverage;
            return this;
        }

        public TVEntityBuilder setVoteCount(Integer voteCount) {
            tvEntity.voteCount = voteCount;
            return this;
        }

        public TVEntityBuilder setTitle(String title) {
            tvEntity.title = title;
            return this;
        }

        public TVEntityBuilder setOverview(String overview) {
            tvEntity.overview = overview;
            return this;
        }

        public TVEntityBuilder setGenreIds(List<Integer> genreIds) {
            tvEntity.genreIds = genreIds;
            return this;
        }

        public TVEntityBuilder setType(TYPE type) {
            tvEntity.type = type;
            return this;
        }

        public TVEntityBuilder setReleaseDate(String releaseDate) {
            tvEntity.releaseDate = releaseDate;
            return this;
        }

        public TVEntity create() {
            return tvEntity;
        }
    }
}
