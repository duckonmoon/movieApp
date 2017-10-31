package movies.test.softserve.movies.event;

import movies.test.softserve.movies.entity.Code;

/**
 * Created by rkrit on 31.10.17.
 */

public interface OnInfoUpdatedListener {
    void OnInfoUpdated(float rating);
}
