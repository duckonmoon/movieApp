package movies.test.softserve.movies.event;

import java.util.List;

import movies.test.softserve.movies.entity.TVEntity;

/**
 * Created by root on 12.12.17.
 */

public interface OnSimilarTVEntitiesGetListener {
    void onSimilarTVEntitiesGetListener(List<TVEntity> tvEntities);
}
