package movies.test.softserve.movies.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.MovieRecyclerViewAdapter;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.service.DBHelperService;
import movies.test.softserve.movies.service.DBMovieService;
import movies.test.softserve.movies.util.StartActivityClass;

public class WatchedFragment extends Fragment {


    private static final String BUNDLE_RECYCLER_LAYOUT = "watch.fragment.recycler.layout";

    private RecyclerView mRecyclerView;

    private DBMovieService dbService = DBMovieService.getInstance();
    private DBHelperService helperService = new DBHelperService();

    public WatchedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watched_movie_list, container, false);

        mRecyclerView = (RecyclerView) view;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet){
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        }
        List<TVEntity> listOfMovies = new ArrayList<>();
        listOfMovies.addAll(dbService.getAllMovies());
        listOfMovies.addAll(dbService.getAllTVShows());
        mRecyclerView.setAdapter(new MovieRecyclerViewAdapter(listOfMovies, new MovieRecyclerViewAdapter.OnMovieSelect() {
            @Override
            public void OnMovieSelected(TVEntity mov) {
                StartActivityClass.startDetailsActivity(getActivity(), mov);
            }
        }, movie -> {
            if (helperService.toDoWithFavourite(movie)) {
                Snackbar.make(mRecyclerView, R.string.added_to_favourite, Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(mRecyclerView, R.string.removed_from_favourite, Snackbar.LENGTH_SHORT)
                        .show();



            }
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }));
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
        List<TVEntity> listOfMovies = new ArrayList<>();
        listOfMovies.addAll(dbService.getAllMovies());
        listOfMovies.addAll(dbService.getAllTVShows());
        ((MovieRecyclerViewAdapter) mRecyclerView.getAdapter()).setMovies(listOfMovies);
        mRecyclerView.getAdapter().notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }
}
