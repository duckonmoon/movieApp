package movies.test.softserve.movies.service;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

import movies.test.softserve.movies.activity.MovieDetailsActivity;
import movies.test.softserve.movies.activity.TVShowDetailsActivity;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.entity.TVShow;

/**
 * Created by rkrit on 17.11.17.
 */

public class StartActivityClass {
    private StartActivityClass() {
    }

    public static void startMovieDetailsActivity(Activity activity, Movie movie) {
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
    }

    public static void startTVShowDetailsActivity(Activity activity, TVShow tvShow) {
        Intent intent = new Intent(activity, TVShowDetailsActivity.class);
        intent.putExtra(TVShowDetailsActivity.ID, tvShow.getId());
        intent.putExtra(TVShowDetailsActivity.NAME, tvShow.getTitle());
        intent.putExtra(TVShowDetailsActivity.POSTER_PATH, tvShow.getPosterPath());
        intent.putExtra(TVShowDetailsActivity.VOTE_COUNT, tvShow.getVoteCount());
        intent.putExtra(TVShowDetailsActivity.VOTE_AVERAGE, tvShow.getVoteAverage());
        intent.putExtra(TVShowDetailsActivity.OVERVIEW, tvShow.getOverview());
        activity.startActivity(intent);
    }
}
