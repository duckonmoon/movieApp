package movies.test.softserve.movies.event;

@FunctionalInterface
public interface AddedItemsEvent {
    void onItemsAdded(String message);
}
