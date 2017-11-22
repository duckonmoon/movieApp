package movies.test.softserve.movies.service;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

import movies.test.softserve.movies.activity.MovieDetailsActivity;
import movies.test.softserve.movies.activity.SearchActivity;
import movies.test.softserve.movies.activity.TVShowDetailsActivity;
import movies.test.softserve.movies.entity.Genre;
import movies.test.softserve.movies.entity.ProductionCompany;
import movies.test.softserve.movies.entity.TVEntity;

/**
 * Created by rkrit on 17.11.17.
 */

public class StartActivityClass {
    private StartActivityClass() {
    }

    public static void startDetailsActivity(Activity activity, TVEntity movie){
        if (movie.getType() == TVEntity.TYPE.MOVIE){
            Intent intent = new Intent(activity, MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsActivity.ID, movie.getId());
            intent.putExtra(MovieDetailsActivity.TITLE, movie.getTitle());
            intent.putExtra(MovieDetailsActivity.POSTER_PATH, movie.getPosterPath());
            intent.putExtra(MovieDetailsActivity.RELEASE_DATE, movie.getReleaseDate());
            intent.putExtra(MovieDetailsActivity.VOTE_COUNT, movie.getVoteCount());
            intent.putExtra(MovieDetailsActivity.VOTE_AVERAGE, movie.getVoteAverage());
            intent.putExtra(MovieDetailsActivity.OVERVIEW, movie.getOverview());
            intent.putExtra(MovieDetailsActivity.GENRES, (ArrayList) movie.getGenreIds());
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(activity, TVShowDetailsActivity.class);
            intent.putExtra(TVShowDetailsActivity.ID, movie.getId());
            intent.putExtra(TVShowDetailsActivity.NAME, movie.getTitle());
            intent.putExtra(TVShowDetailsActivity.POSTER_PATH, movie.getPosterPath());
            intent.putExtra(TVShowDetailsActivity.VOTE_COUNT, movie.getVoteCount());
            intent.putExtra(TVShowDetailsActivity.VOTE_AVERAGE, movie.getVoteAverage());
            intent.putExtra(TVShowDetailsActivity.OVERVIEW, movie.getOverview());
            activity.startActivity(intent);
        }
    }

    public static void startActivitySearch(Activity activity, Genre genre){
        Intent intent = new Intent(activity,SearchActivity.class);
        intent.putExtra(SearchActivity.SEARCH_PARAM,SearchActivity.GENRES);
        intent.putExtra(SearchActivity.ID,genre.getId());
        intent.putExtra(SearchActivity.NAME,genre.getName());
        activity.startActivity(intent);
    }

    public static void startActivitySearch(Activity activity, ProductionCompany company){
        Intent intent = new Intent(activity,SearchActivity.class);
        intent.putExtra(SearchActivity.SEARCH_PARAM,SearchActivity.COMPANIES);
        intent.putExtra(SearchActivity.ID,company.getId());
        intent.putExtra(SearchActivity.NAME,company.getName());
        activity.startActivity(intent);
    }

    public static void startWebIntent(Activity activity,String homepage){
        Uri webPage = Uri.parse(homepage);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
        activity.startActivity(webIntent);
    }
}
