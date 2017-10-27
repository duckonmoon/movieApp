package movies.test.softserve.movies.controllers;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.event.AddedItemsEvent;
import movies.test.softserve.movies.repository.MoviesRepository;
import movies.test.softserve.movies.service.DBService;
import movies.test.softserve.movies.service.MovieReaderDbHelper;

/**
 * Created by rkrit on 25.10.17.
 */

public class MainController extends Application implements Observer{
    private List<Movie> movies;
    private Integer page;
    private MoviesRepository moviesRepository;
    private String errorMessage;
    private AddedItemsEvent eventListener;
    private MovieReaderDbHelper movieReaderDbHelper;
    private SQLiteDatabase database;


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
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MoviesRepository){
            if (moviesRepository.getMovieList()!= null) {
                movies.addAll(moviesRepository.getMovieList());
                errorMessage = null;
                page += 1;
                if (eventListener!=null) {
                    eventListener.onItemsAdded();
                }
            }
            else {
                errorMessage = ((MoviesRepository) o).getMessage();
                if (eventListener!=null) {
                    eventListener.onItemsAdded();
                }
            }
        }
    }

    public void setAddedItemsEventListener(AddedItemsEvent addedItemsEventListener){
        eventListener = addedItemsEventListener;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public Integer getPage() {
        return page;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static MainController getInstance(){
        return INSTANCE;
    }

    public void requestMore() {
        moviesRepository.tryToGetAllMovies();
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
}
