package movies.test.softserve.movies.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.adapter.MyMovieRecyclerViewAdapter;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.entity.TVEntity;
import movies.test.softserve.movies.entity.TVShow;
import movies.test.softserve.movies.service.DBMovieService;
import movies.test.softserve.movies.service.StartActivityClass;

public class WatchedFragment extends Fragment {


    private static final String BUNDLE_RECYCLER_LAYOUT = "watch.fragment.recycler.layout";

    private RecyclerView mRecyclerView;

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

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            List< TVEntity > listOfMovies =  new ArrayList<>();
            listOfMovies.addAll(DBMovieService.getInstance().getAllMovies());
            listOfMovies.addAll(DBMovieService.getInstance().getAllTVShows());
            mRecyclerView.setAdapter(new MyMovieRecyclerViewAdapter(listOfMovies, new MyMovieRecyclerViewAdapter.OnMovieSelect() {
                @Override
                public void OnMovieSelected(TVEntity mov) {
                    if (mov instanceof Movie) {
                        StartActivityClass.startMovieDetailsActivity(getActivity(),(Movie) mov);
                    } else if (mov instanceof TVShow){
                        StartActivityClass.startTVShowDetailsActivity(getActivity(),(TVShow) mov);
                    }
                }
            }, new MyMovieRecyclerViewAdapter.OnFavouriteClick() {
                @Override
                public void onFavouriteClick(TVEntity movie) {

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
        List< TVEntity > listOfMovies =  new ArrayList<>();
        listOfMovies.addAll(DBMovieService.getInstance().getAllMovies());
        listOfMovies.addAll(DBMovieService.getInstance().getAllTVShows());
        ((MyMovieRecyclerViewAdapter) mRecyclerView.getAdapter()).setMovies(listOfMovies);
        mRecyclerView.getAdapter().notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

}
