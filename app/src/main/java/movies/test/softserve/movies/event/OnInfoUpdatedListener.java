package movies.test.softserve.movies.event;

@FunctionalInterface
public interface OnInfoUpdatedListener {
    void OnInfoUpdated(float rating);
}
