package movies.test.softserve.movies.controller;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import movies.test.softserve.movies.entity.GuestSession;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.event.AddedItemsEvent;
import movies.test.softserve.movies.event.OnSessionGetListener;
import movies.test.softserve.movies.repository.MoviesRepository;
import movies.test.softserve.movies.repository.TVShowsRepository;
import movies.test.softserve.movies.service.MovieReaderDbHelper;
import movies.test.softserve.movies.service.MovieService;
/**
 * Created by rkrit on 25.10.17.
 */

public class MainController extends Application implements Observer {
    private List<Movie> movies;
    private Integer page;
    private MoviesRepository moviesRepository;
    private AddedItemsEvent eventListener;
    private MovieReaderDbHelper movieReaderDbHelper;
    private SQLiteDatabase database;
    private MovieService movieService;
    private GuestSession guestSession;


    private static MainController INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        movies = new ArrayList<>();
        page = 1;
        movieReaderDbHelper = new MovieReaderDbHelper(getApplicationContext());
        database = movieReaderDbHelper.getWritableDatabase();
        moviesRepository = MoviesRepository.getInstance();
        moviesRepository.addObserver(this);
        movieService = MovieService.getInstance();
        movieService.addSessionListener(new OnSessionGetListener() {
            @Override
            public void onSessionGet(GuestSession session) {
                guestSession = session;
            }
        });
        TVShowsRepository.getInstance().tryToGetTVShows();
    }



    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MoviesRepository) {
            if (moviesRepository.getMovieList() != null) {
                movies.addAll(moviesRepository.getMovieList());
                page += 1;
                if (eventListener != null) {
                    eventListener.onItemsAdded(null);
                }
            } else {
                if (eventListener != null) {
                    eventListener.onItemsAdded(((MoviesRepository) o).getMessage());
                }
            }
        }
    }

    public void setAddedItemsEventListener(AddedItemsEvent addedItemsEventListener) {
        eventListener = addedItemsEventListener;
    }

    public void removeAddedItemsEventListener() {
        eventListener = null;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public Integer getPage() {
        return page;
    }

    public static MainController getInstance() {
        return INSTANCE;
    }

    public void requestMore() {
        moviesRepository.tryToGetAllMovies();
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public GuestSession getGuestSession() {
        return guestSession;
    }

}
