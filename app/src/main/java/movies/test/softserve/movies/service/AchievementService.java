package movies.test.softserve.movies.service;

import movies.test.softserve.movies.entity.Achievement;

/**
 * Created by rkrit on 16.11.17.
 */

public class AchievementService {

    private DBMovieService service;

    public AchievementService() {
        service = DBMovieService.getInstance();
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
