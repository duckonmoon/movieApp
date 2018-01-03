package movies.test.softserve.movies.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by root on 15.12.17.
 */

@Entity(tableName = "genre", foreignKeys = @ForeignKey(entity = Movie.class,
        parentColumns = "id", childColumns = "movie_id", onDelete = 5), indices = {@Index(value = {"genre_id"}),@Index(value = {"movie_id"})})
public class Genre {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "genre_id")
    private Integer genreId;

    @ColumnInfo(name = "movie_id")
    private Integer movieId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }
}
