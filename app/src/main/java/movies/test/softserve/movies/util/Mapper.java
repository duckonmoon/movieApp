package movies.test.softserve.movies.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.db.entity.Genre;
import movies.test.softserve.movies.db.entity.MovieFirebaseDTO;
import movies.test.softserve.movies.db.entity.MovieWithTheGenre;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.FullTVShow;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.entity.TVShow;

/**
 * Created by rkrit on 22.11.17.
 */

public final class Mapper {
    public static final Boolean FAVOURITE = true;
    public static final Boolean NOT_FAVOURITE = false;

    private Mapper() {
    }

    public static List<TVEntity> mapFromMovieToTVEntity(@NonNull List<? extends Movie> movies) {
        List<TVEntity> entities = new ArrayList<>();
        for (Movie movie :
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

    public static List<TVEntity> mapFromTVShowToTVEntity(@NonNull List<? extends TVShow> tvShows) {
        List<TVEntity> entities = new ArrayList<>();
        for (TVShow tvShow :
                tvShows) {
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

    public static movies.test.softserve.movies.db.entity.Movie mapFromTVEntityToDbMovie(TVEntity entity, boolean isFavourite) {
        movies.test.softserve.movies.db.entity.Movie movie = new movies.test.softserve.movies.db.entity.Movie();
        movie.setId(entity.getId());
        movie.setFavourite(isFavourite ? 1 : 0);
        movie.setWatched(1);
        movie.setImage(entity.getPosterPath());
        movie.setVoteCount(entity.getVoteCount());
        movie.setOverview(entity.getOverview());
        movie.setVoteAverage(entity.getVoteAverage());
        movie.setReleaseDate(entity.getReleaseDate());
        movie.setType(entity.getType().toString());
        movie.setTitle(entity.getTitle());
        return movie;
    }

    public static List<TVEntity> mapFromMovieWithGenreToTVEntity(List<MovieWithTheGenre> movies) {
        List<TVEntity> entities = new ArrayList<>();
        for (MovieWithTheGenre movie :
                movies) {
            TVEntity tvEntity = new TVEntity();
            tvEntity.setId(movie.getMovie().getId());
            tvEntity.setReleaseDate(movie.getMovie().getReleaseDate());
            tvEntity.setType(movie.getMovie().getType().contains("M") ? TVEntity.TYPE.MOVIE : TVEntity.TYPE.TV_SHOW);
            tvEntity.setVoteAverage(movie.getMovie().getVoteAverage());
            tvEntity.setVoteCount(movie.getMovie().getVoteCount());
            tvEntity.setTitle(movie.getMovie().getTitle());
            tvEntity.setOverview(movie.getMovie().getOverview());
            tvEntity.setPosterPath(movie.getMovie().getImage());
            tvEntity.setGenreIds(mapFromGenresToIntegers(movie.getGenres()));
            entities.add(tvEntity);
        }
        return entities;
    }

    public static List<Genre> mapFromIntegersToDbGenres(List<Integer> genres, TVEntity tvEntity) {
        List<Genre> genreList = new ArrayList<>();
        for (Integer genr :
                genres) {
            Genre genre = new Genre();
            genre.setMovieId(tvEntity.getId());
            genre.setGenreId(genr);
            genreList.add(genre);
        }
        return genreList;
    }

    public static List<Integer> mapFromGenresToIntegers(List<Genre> genres) {
        List<Integer> genreList = new ArrayList<>();
        for (Genre genre :
                genres) {
            genreList.add(genre.getGenreId());
        }
        return genreList;
    }

    public static List<Genre> mapFromGenresToGenres(List<movies.test.softserve.movies.entity.Genre> genres, MovieFirebaseDTO movieDTO) {
        List<Genre> genreList = new ArrayList<>();
        for (movies.test.softserve.movies.entity.Genre genre :
                genres) {
            Genre dbGenre = new Genre();
            dbGenre.setGenreId(genre.getId());
            dbGenre.setMovieId(movieDTO.getId());
            genreList.add(dbGenre);
        }
        return genreList;
    }

    public static movies.test.softserve.movies.db.entity.Movie mapFrom2EntitytoDbMovie(
            FullMovie movie, MovieFirebaseDTO movieFirebaseDTO) {
        movies.test.softserve.movies.db.entity.Movie tventity = new movies.test.softserve.movies.db.entity.Movie();
        tventity.setWatched(1);
        tventity.setFavourite(movieFirebaseDTO.getFavourite());
        tventity.setType(movieFirebaseDTO.getType());
        tventity.setId(movieFirebaseDTO.getId());
        tventity.setTitle(movie.getTitle());
        tventity.setOverview(movie.getOverview());
        tventity.setImage(movie.getPosterPath());
        tventity.setVoteCount(movie.getVoteCount());
        tventity.setVoteAverage(movie.getVoteAverage());
        tventity.setReleaseDate(movie.getReleaseDate());
        return tventity;
    }

    public static movies.test.softserve.movies.db.entity.Movie mapFrom2EntitytoDbMovie(
            FullTVShow movie, MovieFirebaseDTO movieFirebaseDTO) {
        movies.test.softserve.movies.db.entity.Movie tventity = new movies.test.softserve.movies.db.entity.Movie();
        tventity.setWatched(1);
        tventity.setFavourite(movieFirebaseDTO.getFavourite());
        tventity.setType(movieFirebaseDTO.getType());
        tventity.setId(movieFirebaseDTO.getId());
        tventity.setTitle(movie.getName());
        tventity.setOverview(movie.getOverview());
        tventity.setImage(movie.getPosterPath());
        tventity.setVoteCount(movie.getVoteCount());
        tventity.setVoteAverage(movie.getVoteAverage());
        return tventity;
    }
}
