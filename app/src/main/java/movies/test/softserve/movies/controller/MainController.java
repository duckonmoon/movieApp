package movies.test.softserve.movies.controller;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.entity.Achievement;
import movies.test.softserve.movies.entity.Genre;
import movies.test.softserve.movies.entity.GuestSession;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.event.AddedItemsEvent;
import movies.test.softserve.movies.event.OnAchievementDoneListener;
import movies.test.softserve.movies.event.OnSessionGetListener;
import movies.test.softserve.movies.repository.MoviesRepository;
import movies.test.softserve.movies.repository.TVShowsRepository;
import movies.test.softserve.movies.service.MovieReaderDbHelper;
import movies.test.softserve.movies.service.MovieService;
import movies.test.softserve.movies.util.AchievementService;

/**
 * Created by rkrit on 25.10.17.
 */

public class MainController extends Application implements Observer, OnAchievementDoneListener {
    private List<TVEntity> movies;
    private List<Genre> genres = new ArrayList<>();
    private Integer page;
    private MoviesRepository moviesRepository;
    private AddedItemsEvent eventListener;
    private MovieReaderDbHelper movieReaderDbHelper;
    private AchievementService achievementService;
    private SQLiteDatabase database;
    private MovieService movieService;
    private GuestSession guestSession;


    private static MainController INSTANCE;


    private Activity currentContext;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        movies = new ArrayList<>();
        page = 1;
        movieReaderDbHelper = new MovieReaderDbHelper(getApplicationContext());
        database = movieReaderDbHelper.getWritableDatabase();
        moviesRepository = MoviesRepository.getInstance();
        achievementService = AchievementService.getInstance();
        achievementService.addListener(this);
        moviesRepository.addObserver(this);
        movieService = MovieService.getInstance();
        movieService.addSessionListener(new OnSessionGetListener() {
            @Override
            public void onSessionGet(GuestSession session) {
                guestSession = session;
            }
        });
        movieService.tryToGetSession();
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

    public List<TVEntity> getMovies() {
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

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public Activity getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(Activity currentContext) {
        this.currentContext = currentContext;
    }

    @Override
    public void onAchievementDone(Achievement achievement) {
        try {
            LayoutInflater inflater = currentContext.getLayoutInflater();
            View view = inflater.inflate(R.layout.alert_dialog_layout,null);
            ImageView image = view.findViewById(R.id.achieve_image);
            image.setImageResource(achievement.getResourceId());
            TextView title = view.findViewById(R.id.achieve_title);
            title.setText(achievement.getTitle());
            TextView text = view.findViewById(R.id.achieve_text);
            text.setText(achievement.getDescription());
            new AlertDialog.Builder(currentContext)
                    .setView(view)
                    .setTitle(getString(R.string.achievement_unlocked))
                    .setPositiveButton(R.string.hooray, (dialog, which) -> {

                    })
                    .show();
        } catch (Exception e) {
            Log.e("Smth went wrong", e.getMessage());
        }
    }
}
