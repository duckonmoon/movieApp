package movies.test.softserve.movies.controller;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import movies.test.softserve.movies.entity.AppToken;
import movies.test.softserve.movies.entity.GuestSession;
import movies.test.softserve.movies.entity.LoginSession;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.event.AddedItemsEvent;
import movies.test.softserve.movies.event.OnAppTokenGetListener;
import movies.test.softserve.movies.event.OnLoginSessionGetListener;
import movies.test.softserve.movies.event.OnSessionGetListener;
import movies.test.softserve.movies.repository.MoviesRepository;
import movies.test.softserve.movies.service.MovieReaderDbHelper;
import movies.test.softserve.movies.service.MovieService;

/**
 * Created by rkrit on 25.10.17.
 */

public class MainController extends Application implements Observer {
    private List<Movie> movies;
    private Integer page;
    private MoviesRepository moviesRepository;
    private String errorMessage;
    private AddedItemsEvent eventListener;
    private MovieReaderDbHelper movieReaderDbHelper;
    private SQLiteDatabase database;
    private MovieService movieService;
    private GuestSession guestSession;
    private LoginSession loginSession;
    private AppToken appToken;
    private Boolean tokenApproved = false;


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
        movieService.addOnAppTokenListener(new OnAppTokenGetListener() {
            @Override
            public void onAppTokenGet(@NotNull AppToken appToken) {
                MainController.this.appToken = appToken;
            }
        });
        movieService.addOnLoginSessionGetListener(new OnLoginSessionGetListener() {
            @Override
            public void OnLoginSessionGet(@NotNull LoginSession loginSession) {
                if (loginSession!=null) {
                    MainController.this.loginSession = loginSession;
                }else{
                    appToken = null;
                    movieService.tryToGetToken();
                }
            }
        });
        movieService.tryToGetToken();
        movieService.tryToGetSession();
    }

    public void LogOut() {
        loginSession = null;
        appToken = null;
        movieService.tryToGetToken();
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MoviesRepository) {
            if (moviesRepository.getMovieList() != null) {
                movies.addAll(moviesRepository.getMovieList());
                errorMessage = null;
                page += 1;
                if (eventListener != null) {
                    eventListener.onItemsAdded();
                }
            } else {
                errorMessage = ((MoviesRepository) o).getMessage();
                if (eventListener != null) {
                    eventListener.onItemsAdded();
                }
            }
        }
    }

    public void setAddedItemsEventListener(AddedItemsEvent addedItemsEventListener) {
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

    public AppToken getAppToken() {
        return appToken;
    }

    public Boolean getTokenApproved() {
        return tokenApproved;
    }

    public void isTokenApproved() {
        this.tokenApproved = true;
    }

    public LoginSession getLoginSession() {
        return loginSession;
    }

    public void setLoginSession(LoginSession loginSession) {
        this.loginSession = loginSession;
    }


}
