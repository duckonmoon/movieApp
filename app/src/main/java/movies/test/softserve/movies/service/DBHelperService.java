package movies.test.softserve.movies.service;

import movies.test.softserve.movies.entity.TVEntity;

/**
 * Created by rkrit on 21.11.17.
 */

public class DBHelperService {
    private DbMovieServiceRoom service;

    public DBHelperService() {
        service = DbMovieServiceRoom.Companion.getInstance();
    }


    /**need to be asynchronous*/
    public boolean toDoWithFavourite(TVEntity movie) {
        if (!service.checkIfIsFavourite(movie)) {
            if (!service.checkIfExists(movie)) {
                service.insertFavouriteTVEntity(movie);
            } else {
                service.setFavourite(movie);
            }

            return true;
        } else {
            service.cancelFavourite(movie);
            return false;
        }
    }

    /**need to be asynchronous*/
    public Watched toDoWithWatched(TVEntity movie) {
        if (!service.checkIfExists(movie)) {
            service.insertTVEntity(movie);
            return Watched.WATCHED;
        } else {
            if (service.checkIfIsFavourite(movie)) {
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
