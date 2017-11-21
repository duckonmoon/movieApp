package movies.test.softserve.movies.service;

import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.entity.TVShow;

/**
 * Created by rkrit on 21.11.17.
 */

public class DBHelperService {
    DBMovieService service;

    public DBHelperService() {
        service = DBMovieService.getInstance();
    }

    public enum Watched {
        WATCHED,
        FAVOURITE,
        CANCELED
    }


    public boolean toDoWithFavourite(Movie movie) {
        if (!service.checkIfIsFavourite(movie.getId())) {
            if (!service.checkIfExists(movie.getId())) {
                service.insertMovieToFavourite(movie.getId(),
                        movie.getTitle(),
                        movie.getVoteAverage().floatValue(),
                        movie.getVoteCount(),
                        movie.getOverview(),
                        movie.getReleaseDate(),
                        movie.getPosterPath(),
                        movie.getGenreIds());
            } else {
                service.setFavourite(movie.getId());
            }
            return true;
        } else {
            service.cancelFavourite(movie.getId());
            return false;
        }
    }

    public Watched toDoWithWatched(Movie movie) {
        if (!service.checkIfExists(movie.getId())) {
            service.addMovieToDb(movie.getId(),
                    movie.getTitle(),
                    movie.getVoteAverage().floatValue(),
                    movie.getVoteCount(),
                    movie.getOverview(),
                    movie.getReleaseDate(),
                    movie.getPosterPath(),
                    movie.getGenreIds()
            );
            return Watched.WATCHED;
        } else {
            if (service.checkIfIsFavourite(movie.getId())) {
                return Watched.FAVOURITE;
            } else {
                return Watched.CANCELED;
            }
        }
    }

    public boolean toDoWithFavourite(TVShow tvShow) {
        if (!service.checkIfIsFavourite(tvShow.getId())) {
            if (!service.checkIfExists(tvShow.getId())) {
                service.insertTVShowToFavourite(tvShow.getId(),
                        tvShow.getName(),
                        tvShow.getVoteAverage().floatValue(),
                        tvShow.getVoteCount(),
                        tvShow.getOverview(),
                        tvShow.getPosterPath());
            } else {
                service.setFavourite(tvShow.getId());
            }
            return true;
        } else {
            service.cancelFavourite(tvShow.getId());
            return false;
        }
    }

    public Watched toDoWithWatched(TVShow tvShow) {
        if (!service.checkIfExists(tvShow.getId())) {
            service.addTVShowToDb(tvShow.getId(),
                    tvShow.getTitle(),
                    tvShow.getVoteAverage().floatValue(),
                    tvShow.getVoteCount(),
                    tvShow.getOverview(),
                    tvShow.getPosterPath()
            );
            return Watched.WATCHED;
        } else {
            if (service.checkIfIsFavourite(tvShow.getId())) {
                return Watched.FAVOURITE;
            } else {
                return Watched.CANCELED;
            }
        }
    }
}
