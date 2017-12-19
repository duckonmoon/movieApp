package movies.test.softserve.movies.event;

import movies.test.softserve.movies.entity.Achievement;

@FunctionalInterface
public interface OnAchievementDoneListener {
    void onAchievementDone(Achievement achievement);
}
