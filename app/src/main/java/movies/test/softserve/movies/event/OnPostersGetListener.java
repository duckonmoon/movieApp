package movies.test.softserve.movies.event;

import java.util.List;

import movies.test.softserve.movies.entity.Backdrop;
import movies.test.softserve.movies.entity.Poster;

/**
 * Created by root on 11.12.17.
 */

public interface OnPostersGetListener {
    void onPostersGet(List<Poster> posters);
    void onBackDropGet(List<Backdrop> backdrops);
}
