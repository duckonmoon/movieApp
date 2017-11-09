package movies.test.softserve.movies.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.activity.MovieDetailsActivity;
import movies.test.softserve.movies.adapter.MyMovieRecyclerViewAdapter;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.service.DBMovieService;


public class MovieFragment extends Fragment {

    private static final String BUNDLE_RECYCLER_LAYOUT = "fragment.recycler.layout";

    private RecyclerView mRecyclerView;

    public MovieFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setAdapter(new MyMovieRecyclerViewAdapter(DBMovieService.getInstance().getFavouriteMovies(), new MyMovieRecyclerViewAdapter.OnMovieSelect() {
                @Override
                public void OnMovieSelected(Movie movie) {
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra(MovieDetailsActivity.ID, movie.getId());
                    intent.putExtra(MovieDetailsActivity.TITLE, movie.getTitle());
                    intent.putExtra(MovieDetailsActivity.POSTER_PATH, movie.getPosterPath());
                    intent.putExtra(MovieDetailsActivity.RELEASE_DATE, movie.getReleaseDate());
                    intent.putExtra(MovieDetailsActivity.VOTE_COUNT, movie.getVoteCount());
                    intent.putExtra(MovieDetailsActivity.VOTE_AVERAGE, movie.getVoteAverage());
                    intent.putExtra(MovieDetailsActivity.OVERVIEW, movie.getOverview());
                    getActivity().startActivity(intent);
                }
            }, new MyMovieRecyclerViewAdapter.OnFavouriteClick() {
                @Override
                public void onFavouriteClick(Movie movie) {

                }
            }));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    private void setAdapter() {
        ((MyMovieRecyclerViewAdapter) mRecyclerView.getAdapter()).setMovies(DBMovieService.getInstance().getFavouriteMovies());
        mRecyclerView.getAdapter().notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }
}
