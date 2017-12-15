package movies.test.softserve.movies.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import movies.test.softserve.movies.activity.MovieDetailsActivity;
import movies.test.softserve.movies.activity.SearchActivity;
import movies.test.softserve.movies.activity.SimilarActivity;
import movies.test.softserve.movies.activity.TVShowDetailsActivity;
import movies.test.softserve.movies.activity.VideoActivity;
import movies.test.softserve.movies.entity.Genre;
import movies.test.softserve.movies.entity.ProductionCompany;
import movies.test.softserve.movies.entity.TVEntity;

/**
 * Created by rkrit on 17.11.17.
 */

public class StartActivityClass {
    private StartActivityClass() {
    }

    public static void startDetailsActivity(Activity activity, TVEntity movie) {
        if (movie.getType() == TVEntity.TYPE.MOVIE) {
            Intent intent = new Intent(activity, MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsActivity.TV_ENTITY, movie);
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(activity, TVShowDetailsActivity.class);
            intent.putExtra(TVShowDetailsActivity.TV_ENTITY, movie);
            activity.startActivity(intent);
        }
    }

    public static void startActivitySearch(Activity activity, Genre genre) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra(SearchActivity.SEARCH_PARAM, SearchActivity.GENRES);
        intent.putExtra(SearchActivity.ID, genre.getId());
        intent.putExtra(SearchActivity.NAME, genre.getName());
        activity.startActivity(intent);
    }

    public static void startActivitySearch(Activity activity, ProductionCompany company) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra(SearchActivity.SEARCH_PARAM, SearchActivity.COMPANIES);
        intent.putExtra(SearchActivity.ID, company.getId());
        intent.putExtra(SearchActivity.NAME, company.getName());
        activity.startActivity(intent);
    }

    public static void startWebIntent(Activity activity, String homepage) {
        Uri webPage = Uri.parse(homepage);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
        activity.startActivity(webIntent);
    }

    public static void startVideosActivity(Activity activity, TVEntity tvEntity) {
        Intent intent = new Intent(activity, VideoActivity.class);
        intent.putExtra(VideoActivity.Companion.getMOVIE_ID(), tvEntity.getId());
        activity.startActivity(intent);
    }

    public static void startSimilarActivity(Activity activity, TVEntity tvEntity) {
        Intent intent = new Intent(activity, SimilarActivity.class);
        intent.putExtra(SimilarActivity.Companion.getMOVIE(), tvEntity);
        activity.startActivity(intent);
    }
}
