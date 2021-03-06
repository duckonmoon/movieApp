package movies.test.softserve.movies.util;

import android.content.Context;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.controller.MainController;

/**
 * Created by rkrit on 14.11.17.
 */

public class RatingService {
    public final static String ADD = "plus";
    public final static String SUB = "minus";
    private final static String MAIN_SHARED_PREF = "main.shared.pref";
    private final static String RATING = "rating";
    private static RatingService INSTANCE;
    private Float rating;
    private Levels lvl;
    private Float progress;
    private MainController controller;
    private List<OnRatingChangeListener> listeners = new ArrayList<>();

    private RatingService() {
        INSTANCE = this;
        controller = MainController.getInstance();
        rating = controller.getSharedPreferences(MAIN_SHARED_PREF, Context.MODE_PRIVATE).getFloat(RATING, 0);
    }

    public static RatingService getInstance() {
        if (INSTANCE == null) {
            new RatingService();
        }
        return INSTANCE;
    }

    public Levels getLvl() {
        reset();
        return lvl;
    }

    public Float getProgress() {
        reset();
        return progress;
    }

    public void change(Float movieRate, String mode) {
        Float value = toRatingPoints(movieRate);
        switch (mode) {
            case ADD:
                rating += value;
                break;
            case SUB:
                rating -= value;
                break;
            default:
                break;
        }
        reset();
        save();
    }

    private void save() {
        controller.getSharedPreferences(MAIN_SHARED_PREF, Context.MODE_PRIVATE).edit().putFloat(RATING, rating).apply();
        for (OnRatingChangeListener listener :
                listeners) {
            listener.onRatingChange(lvl, rating);
        }
    }

    private void reset() {
        int score = 0;
        for (Levels level :
                Levels.values()) {
            score += level.toNextLevel;
            if (rating <= score) {
                lvl = level;
                progress = (rating - score + level.toNextLevel) / lvl.toNextLevel * 100;
                return;
            }
        }
        lvl = Levels.TENTH;
        progress = 100f;
    }

    @NonNull
    @Contract(pure = true)
    private Float toRatingPoints(Float rate) {
        return 15 - rate;
    }

    public void addOnRatingChangeListener(OnRatingChangeListener listener) {
        listeners.add(listener);
    }

    public void removeOnRatingChangeListener(OnRatingChangeListener listener) {
        listeners.remove(listener);
    }

    public void clear() {
        controller.getSharedPreferences(MAIN_SHARED_PREF, Context.MODE_PRIVATE).edit().putFloat(RATING, 0).apply();
        rating = 0f;
    }

    public enum Levels {
        ZERO(100),
        FIRST(200),
        SECOND(300),
        THIRD(400),
        FOURTH(500),
        FIFTH(600),
        SIXTH(700),
        SEVENTH(800),
        EIGHTH(900),
        NINTH(100),
        TENTH(0);

        public final int toNextLevel;

        Levels(int i) {
            toNextLevel = i;
        }
    }

    public interface OnRatingChangeListener {
        void onRatingChange(Levels lvl, Float rating);
    }
}
