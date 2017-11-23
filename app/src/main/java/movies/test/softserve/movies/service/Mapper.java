package movies.test.softserve.movies.service;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.entity.TVShow;

/**
 * Created by rkrit on 22.11.17.
 */

public final class Mapper {
    private Mapper(){}

    public static List<TVEntity> mapFromMovieToTVEntity(@NonNull List<? extends Movie> movies){
        List<TVEntity> entities = new ArrayList<>();
        for (Movie movie:
             movies) {
            TVEntity tvEntity = new TVEntity.TVEntityBuilder()
                    .setId(movie.getId())
                    .setPosterPath(movie.getPosterPath())
                    .setVoteAverage(movie.getVoteAverage())
                    .setVoteCount(movie.getVoteCount())
                    .setTitle(movie.getTitle())
                    .setOverview(movie.getOverview())
                    .setGenreIds(movie.getGenreIds())
                    .setReleaseDate(movie.getReleaseDate())
                    .setType(TVEntity.TYPE.MOVIE)
                    .create();
            entities.add(tvEntity);
        }
        return entities;
    }

    public static List<TVEntity> mapFromTVShowToTVEntity(@NonNull List<? extends TVShow> tvShows){
        List<TVEntity> entities = new ArrayList<>();
        for (TVShow tvShow:
                tvShows){
            TVEntity tvEntity = new TVEntity.TVEntityBuilder()
                    .setId(tvShow.getId())
                    .setPosterPath(tvShow.getPosterPath())
                    .setVoteAverage(tvShow.getVoteAverage())
                    .setVoteCount(tvShow.getVoteCount())
                    .setTitle(tvShow.getName())
                    .setOverview(tvShow.getOverview())
                    .setGenreIds(tvShow.getGenreIds())
                    .setType(TVEntity.TYPE.TV_SHOW)
                    .create();
            entities.add(tvEntity);

        }
        return entities;
    }
}