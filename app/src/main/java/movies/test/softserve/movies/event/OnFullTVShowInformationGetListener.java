package movies.test.softserve.movies.event;

import movies.test.softserve.movies.db.entity.MovieFirebaseDTO;
import movies.test.softserve.movies.entity.FullTVShow;

/**
 * Created by root on 29.12.17.
 */

public interface OnFullTVShowInformationGetListener {
    void onFullTVShowGet(FullTVShow fullTVShow, MovieFirebaseDTO movieFirebaseDTO);
}
