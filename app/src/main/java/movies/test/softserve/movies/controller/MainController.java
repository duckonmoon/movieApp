package movies.test.softserve.movies.controller;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.db.entity.Movie;
import movies.test.softserve.movies.db.entity.MovieFirebaseDTO;
import movies.test.softserve.movies.entity.Achievement;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.FullTVShow;
import movies.test.softserve.movies.entity.Genre;
import movies.test.softserve.movies.entity.GuestSession;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.event.AddedItemsEvent;
import movies.test.softserve.movies.event.InfoUpToDateListener;
import movies.test.softserve.movies.event.OnAchievementDoneListener;
import movies.test.softserve.movies.event.OnFullMovieInformationGet;
import movies.test.softserve.movies.event.OnFullTVShowInformationGetListener;
import movies.test.softserve.movies.event.OnInfoUpdatedListener;
import movies.test.softserve.movies.event.OnSessionGetListener;
import movies.test.softserve.movies.repository.MoviesRepository;
import movies.test.softserve.movies.repository.TVShowsRepository;
import movies.test.softserve.movies.service.AppRoomDatabase;
import movies.test.softserve.movies.service.MovieService;
import movies.test.softserve.movies.util.AchievementService;
import movies.test.softserve.movies.util.Mapper;
import movies.test.softserve.movies.util.RatingService;

public class MainController extends Application implements Observer, OnAchievementDoneListener {
    private static MainController INSTANCE;

    private static final String LAST_DATE_EDIT_SHARED_PREFERENCES = "LAST_DATE_EDIT";

    private List<TVEntity> movies;
    private List<Genre> genres = new ArrayList<>();
    private Integer page;
    private MoviesRepository moviesRepository;
    private TVShowsRepository tvShowsRepository;
    private AddedItemsEvent eventListener;
    private AchievementService achievementService;
    private MovieService movieService;
    private GuestSession guestSession;
    private Activity currentContext;
    private AppRoomDatabase database;
    private DatabaseReference databaseReference;
    private SharedPreferences preferences;

    private InfoUpToDateListener infoListener;

    OnFullMovieInformationGet listener = new OnFullMovieInformationGet() {
        @Override
        public void onMovieGet(FullMovie movie, MovieFirebaseDTO movieDTO) {
            Movie tvEntity = Mapper.mapFrom2EntitytoDbMovie(movie, movieDTO);
            new Thread(() -> {
                database.movieDao().insertMovie(tvEntity);
                database.genreDao().insertGenres(Mapper.mapFromGenresToGenres(movie.getGenres(),movieDTO));
                RatingService.getInstance().change(movie.getVoteAverage().floatValue(),RatingService.ADD);

            }).start();

            toDownload.decrementAndGet();

            checkIfAllDataIsTransported();
        }
    };

    OnFullTVShowInformationGetListener tvShowListener = new OnFullTVShowInformationGetListener() {
        @Override
        public void onFullTVShowGet(FullTVShow fullTVShow, MovieFirebaseDTO movieDTO) {
            Movie tvEntity = Mapper.mapFrom2EntitytoDbMovie(fullTVShow, movieDTO);
            new Thread(() -> database.movieDao().insertMovie(tvEntity)).start();
            RatingService.getInstance().change(fullTVShow.getVoteAverage().floatValue(),RatingService.ADD);
            toDownload.decrementAndGet();

            checkIfAllDataIsTransported();
        }
    };


    private AtomicInteger toDownload = new AtomicInteger();
    private Long lDER;


    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private LiveData<Integer> favourite;

    private int previousValue = 0;
    private HashMap<Object, BooleanHolder> dbObservers = new HashMap<>();

    public static MainController getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(), AppRoomDatabase.class,
                "movies").build();

        mAuth = FirebaseAuth.getInstance();
        INSTANCE = this;
        movies = new ArrayList<>();
        page = 1;
        moviesRepository = MoviesRepository.getInstance();
        moviesRepository.addObserver(this);
        movieService = MovieService.getInstance();
        movieService.addSessionListener(new OnSessionGetListener() {
            @Override
            public void onSessionGet(GuestSession session) {
                guestSession = session;
            }
        });
        movieService.tryToGetSession();
        tvShowsRepository = TVShowsRepository.getInstance();

        favourite = database.movieDao().loadAllFavouriteMovies();
        favourite.observeForever(integer -> {
            if (integer != previousValue) {
                for (BooleanHolder b :
                        dbObservers.values()) {
                    b.aBoolean = Boolean.TRUE;
                }
                previousValue = integer;
            }
            Log.e("Changed", integer.toString());
        });

        user = mAuth.getCurrentUser();
        preferences = getSharedPreferences(LAST_DATE_EDIT_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
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

    public void requestMore() {
        moviesRepository.tryToGetAllMovies();
    }

    public AppRoomDatabase getDatabase() {
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

    public void setCurrentContext(Activity currentContext) {
        this.currentContext = currentContext;
    }

    @Override
    public void onAchievementDone(final Achievement achievement) {
        currentContext.runOnUiThread(() -> show(achievement));

    }

    private void show(Achievement achievement) {
        try {
            LayoutInflater inflater = currentContext.getLayoutInflater();
            View view = inflater.inflate(R.layout.alert_dialog_layout, null);
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

    public void addDbObserver(Object observer) {
        dbObservers.put(observer, new BooleanHolder());
    }

    public void removeDbObserver(Object observer) {
        dbObservers.remove(observer);
    }

    public boolean check(Object observer) {
        return dbObservers.get(observer).aBoolean;
    }

    public void unCheck(Object observer) {
        dbObservers.get(observer).aBoolean = false;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public void signOut() {
        mAuth.signOut();
        user = mAuth.getCurrentUser();
    }

    public void updateInfoFirebase() {
        new Thread(() -> {
            databaseReference.child("allMovie").setValue(database.movieDao().getAllId());
            Long last_change = Calendar.getInstance().getTime().getTime();
            databaseReference.child("last_date_edit").setValue(last_change);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(LAST_DATE_EDIT_SHARED_PREFERENCES, last_change);
            editor.apply();
        }).start();
    }

    public void getLastUpdates(InfoUpToDateListener listen) {
        infoListener = listen;
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        databaseReference.child("last_date_edit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (checkIfLastChangeDateChanged(dataSnapshot)) {
                        new Thread(() -> {
                            deleteEverythingFromDb();

                            getNewInfo();

                        }).start();
                        //TODO Thread.sleep(2000);
                    }
                    else {
                        infoListener.upToDate();
                        infoListener = null;
                    }
                    databaseReference.child("last_date_edit").removeEventListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private boolean checkIfLastChangeDateChanged(DataSnapshot dataSnapshot) {
        Long lastDayEditRemote;
        Long lastDayEditHere;
        lastDayEditRemote = dataSnapshot.getValue(Long.class);
        lDER = lastDayEditRemote == null ? 0L : lastDayEditRemote;

        lastDayEditHere = preferences.getLong(LAST_DATE_EDIT_SHARED_PREFERENCES, 0);
        return !Objects.equals(lastDayEditHere, lastDayEditRemote);
    }

    private void deleteEverythingFromDb() {
        database.genreDao().deleteEverything();
        database.movieDao().deleteEverything();
    }

    private void saveNewChangeDateToSharedPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(LAST_DATE_EDIT_SHARED_PREFERENCES, lDER);
        editor.apply();
    }

    private void getNewInfo() {
        databaseReference.child("allMovie").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<MovieFirebaseDTO> movies = new ArrayList<>();

                movieService.addListener(listener);
                tvShowsRepository.addOnFullTVShowGetListeners(tvShowListener);
                toDownload = new AtomicInteger();
                for (DataSnapshot data :
                        dataSnapshot.getChildren()) {
                    toDownload.incrementAndGet();
                    MovieFirebaseDTO movie = data.getValue(MovieFirebaseDTO.class);
                    movies.add(movie);
                    if (movie.getType().contains("OV")) {
                        movieService.tryToGetMovie(movie.getId(), movie);
                    } else {
                        tvShowsRepository.trytoGetFullTVShow(movie);
                    }
                }
                databaseReference.child("allMovie").removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkIfAllDataIsTransported() {
        if (toDownload.get() == 0) {
            movieService.removeListener(listener);
            tvShowsRepository.removeOnFullTVShowGetListeners(tvShowListener);
            saveNewChangeDateToSharedPreferences();
            infoListener.upToDate();
            infoListener = null;
            achievementService = AchievementService.getInstance();
            achievementService.addListener(this);
        }
    }


    private class BooleanHolder {
        boolean aBoolean;
    }
}
