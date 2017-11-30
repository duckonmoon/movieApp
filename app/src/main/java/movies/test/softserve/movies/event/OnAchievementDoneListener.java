package movies.test.softserve.movies.event;

import movies.test.softserve.movies.entity.Achievement;

/**
 * Created by rkrit on 30.11.17.
 */

public interface OnAchievementDoneListener {
    void onAchievementDone(Achievement achievement);
}
