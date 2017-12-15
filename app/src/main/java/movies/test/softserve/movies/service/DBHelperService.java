package movies.test.softserve.movies.service;

import movies.test.softserve.movies.entity.TVEntity;

/**
 * Created by rkrit on 21.11.17.
 */

public class DBHelperService {
    private DBMovieService service;

    public DBHelperService() {
        service = DBMovieService.getInstance();
    }

    public boolean toDoWithFavourite(TVEntity movie) {
        if (!service.checkIfIsFavourite(movie.getId())) {
            if (!service.checkIfExists(movie.getId())) {
                if (movie.getType() == TVEntity.TYPE.MOVIE) {
                    service.insertMovieToFavourite(movie.getId(),
                            movie.getTitle(),
                            movie.getVoteAverage().floatValue(),
                            movie.getVoteCount(),
                            movie.getOverview(),
                            movie.getReleaseDate(),
                            movie.getPosterPath(),
                            movie.getGenreIds());
                } else {
                    service.insertTVShowToFavourite(movie.getId(),
                            movie.getTitle(),
                            movie.getVoteAverage().floatValue(),
                            movie.getVoteCount(),
                            movie.getOverview(),
                            movie.getPosterPath());
                }
            } else {
                service.setFavourite(movie.getId());
            }
            return true;
        } else {
            service.cancelFavourite(movie.getId());
            return false;
        }
    }

    public Watched toDoWithWatched(TVEntity movie) {
        if (!service.checkIfExists(movie.getId())) {
            if (movie.getType() == TVEntity.TYPE.MOVIE) {
                service.addMovieToDb(movie.getId(),
                        movie.getTitle(),
                        movie.getVoteAverage().floatValue(),
                        movie.getVoteCount(),
                        movie.getOverview(),
                        movie.getReleaseDate(),
                        movie.getPosterPath(),
                        movie.getGenreIds());
            } else {
                service.addTVShowToDb(movie.getId(),
                        movie.getTitle(),
                        movie.getVoteAverage().floatValue(),
                        movie.getVoteCount(),
                        movie.getOverview(),
                        movie.getPosterPath());
            }
            return Watched.WATCHED;
        } else {
            if (service.checkIfIsFavourite(movie.getId())) {
                return Watched.FAVOURITE;
            } else {
                return Watched.CANCELED;
            }
        }
    }


    public enum Watched {
        WATCHED,
        FAVOURITE,
        CANCELED
    }
}
