package movies.test.softserve.movies.event;

import java.util.List;

import movies.test.softserve.movies.entity.Backdrop;
import movies.test.softserve.movies.entity.Poster;

public interface OnPostersGetListener {
    void onPostersGet(List<Poster> posters);

    void onBackDropGet(List<Backdrop> backdrops);
}
