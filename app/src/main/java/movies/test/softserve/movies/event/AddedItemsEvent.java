package movies.test.softserve.movies.event;

/**
 * Created by rkrit on 25.10.17.
 */

public interface AddedItemsEvent {
    void onItemsAdded(String message);
}
