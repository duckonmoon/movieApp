package movies.test.softserve.movies.event;

import movies.test.softserve.movies.entity.GuestSession;

/**
 * Created by rkrit on 31.10.17.
 */

public interface OnSessionGetListener {
    void onSessionGet(GuestSession guestSession);
}
