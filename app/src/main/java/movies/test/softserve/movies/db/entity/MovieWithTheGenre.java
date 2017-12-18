package movies.test.softserve.movies.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * Created by root on 18.12.17.
 */

public class MovieWithTheGenre {
    @Embedded
    private Movie movie;

    @Relation(parentColumn = "id", entityColumn = "movie_id", entity = Genre.class)
    private List<Genre> genres;

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
