package movies.test.softserve.movies.event;

import movies.test.softserve.movies.entity.GuestSession;

@FunctionalInterface
public interface OnSessionGetListener {
    void onSessionGet(GuestSession guestSession);
}
