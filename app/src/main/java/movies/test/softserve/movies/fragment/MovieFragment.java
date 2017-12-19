package movies.test.softserve.movies.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
import movies.test.softserve.movies.service.DbMovieServiceRoom;
import movies.test.softserve.movies.util.StartActivityClass;


public class MovieFragment extends Fragment {

    private static final String BUNDLE_RECYCLER_LAYOUT = "fragment.recycler.layout";

    private RecyclerView mRecyclerView;

    private Handler handler = new Handler();

    private DbMovieServiceRoom dbService = DbMovieServiceRoom.Companion.getInstance();

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

        Context context = view.getContext();
        mRecyclerView = (RecyclerView) view;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        }
        List<TVEntity> listOfMovies = new ArrayList<>();
        mRecyclerView.setAdapter(new MovieRecyclerViewAdapter(listOfMovies, new MovieRecyclerViewAdapter.OnMovieSelect() {
            @Override
            public void OnMovieSelected(TVEntity mov) {
                StartActivityClass.startDetailsActivity(getActivity(), mov);
            }
        }, new MovieRecyclerViewAdapter.OnFavouriteClick() {
            @Override
            public void onFavouriteClick(TVEntity movie, Integer position) {

            }
        }));

        new Thread(() -> {
            listOfMovies.addAll(dbService.getAllFavourite(TVEntity.TYPE.MOVIE));
            listOfMovies.addAll(dbService.getAllFavourite(TVEntity.TYPE.TV_SHOW));
            handler.post(() -> mRecyclerView.getAdapter().notifyDataSetChanged());

        }).start();

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
        new Thread(() -> {
            listOfMovies.addAll(dbService.getAllFavourite(TVEntity.TYPE.MOVIE));
            listOfMovies.addAll(dbService.getAllFavourite(TVEntity.TYPE.TV_SHOW));
            handler.post(() -> {
                ((MovieRecyclerViewAdapter) mRecyclerView.getAdapter()).setMovies(listOfMovies);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            });

        }).start();


    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }
}
