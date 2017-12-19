package movies.test.softserve.movies.event;

import java.util.List;

import movies.test.softserve.movies.entity.TVEntity;

@FunctionalInterface
public interface OnSimilarTVEntitiesGetListener {
    void onSimilarTVEntitiesGetListener(List<TVEntity> tvEntities);
}
