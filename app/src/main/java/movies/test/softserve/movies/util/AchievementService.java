package movies.test.softserve.movies.util;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.entity.Achievement;
import movies.test.softserve.movies.event.OnAchievementDoneListener;
import movies.test.softserve.movies.service.DBMovieService;

/**
 * Created by rkrit on 16.11.17.
 */

public class AchievementService {

    private static AchievementService INSTANCE;
    private DBMovieService service = DBMovieService.getInstance();
    private List<Achievement> achievements = new ArrayList<>();
    private List<OnAchievementDoneListener> listeners = new ArrayList<>();

    private AchievementService() {
        for (Achievement achievement :
                Achievement.Companion.getAchievements()) {
            if (!getAchievementStatus(achievement)) {
                achievements.add(achievement);
            }

        }
    }

    public static AchievementService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AchievementService();
        }
        return INSTANCE;
    }

    public boolean getAchievementStatus(Achievement achievement) {
        if (achievement.getGenre() != null) {
            switch (achievement.getClazz()) {
                case MOVIE:
                    return getMovieAchievement(achievement);
                case TV_SHOW:
                    return getTVShowAchievement(achievement);
                default:
                    return false;
            }
        } else {
            switch (achievement.getClazz()) {
                case MOVIE:
                    return getMovieAchievementWithoutGenre(achievement);
                case TV_SHOW:
                    return getTVShowAchievementWithoutGenre(achievement);
                default:
                    return false;
            }
        }


    }

    public void checkWhatAchievementsIsDone() {
        Runnable runnable = () -> {
            List<Achievement> achievementsToDelete = new ArrayList<>();
            for (Achievement achievement :
                    achievements) {
                if (getAchievementStatus(achievement)) {
                    for (OnAchievementDoneListener listener :
                            listeners) {
                        listener.onAchievementDone(achievement);
                        achievementsToDelete.add(achievement);
                    }
                }
            }
            achievements.removeAll(achievementsToDelete);
        };
        runnable.run();
    }


    public void addListener(OnAchievementDoneListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OnAchievementDoneListener listener) {
        listeners.remove(listener);
    }

    private boolean getTVShowAchievementWithoutGenre(Achievement achievement) {
        return service.getTVShowsSize() >= achievement.getNumber();
    }

    private boolean getMovieAchievementWithoutGenre(Achievement achievement) {
        return service.getMoviesSize() >= achievement.getNumber();
    }

    private boolean getTVShowAchievement(Achievement achievement) {
        return false;
    }

    private boolean getMovieAchievement(Achievement achievement) {
        return service.getMoviesSizeWithGenre(achievement.getGenre()) >= achievement.getNumber();
    }

}
