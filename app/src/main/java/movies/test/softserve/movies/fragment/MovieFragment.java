package movies.test.softserve.movies.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Observable;
import java.util.Observer;

import movies.test.softserve.movies.R;
import movies.test.softserve.movies.entity.FullMovie;
import movies.test.softserve.movies.entity.Movie;
import movies.test.softserve.movies.service.MovieService;


public class MovieFragment extends DialogFragment implements Observer {
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String RELEASE_DATE = "release date";
    private static final String VOTE_AVERAGE = "vote average";
    private static final String VOTE_COUNT = "vote count";
    private static final String POSTER_PATH = "poster path";
    private static final String OVERVIEW = "overview";

    private Integer id;
    private String title;
    private String releaseDate;
    private Double voteAverage;
    private Integer voteCount;
    private String posterPath;
    private String overview;
    private FullMovie fullMovie;

    private ImageView imageView;

    private MovieService service;


    public MovieFragment() {
    }

    public static MovieFragment newInstance(Movie movie) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putInt(ID, movie.getId());
        args.putString(TITLE, movie.getTitle());
        args.putString(RELEASE_DATE, movie.getReleaseDate());
        args.putDouble(VOTE_AVERAGE, movie.getVoteAverage());
        args.putInt(VOTE_COUNT, movie.getVoteCount());
        args.putString(POSTER_PATH, movie.getPosterPath());
        args.putString(OVERVIEW, movie.getOverview());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ID);
            title = getArguments().getString(TITLE);
            releaseDate = getArguments().getString(RELEASE_DATE);
            voteAverage = getArguments().getDouble(VOTE_AVERAGE);
            voteCount = getArguments().getInt(VOTE_COUNT);
            posterPath = getArguments().getString(POSTER_PATH);
            overview = getArguments().getString(OVERVIEW);
            service = MovieService.getInstance();
            service.addObserver(this);
            service.tryToGetMovie(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        imageView = view.findViewById(R.id.movie_image_detail);
        Picasso
                .with(imageView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + posterPath)
                .into(imageView);
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
    public void update(Observable o, Object arg) {
        fullMovie = ((MovieService) o).getFullMovie();
    }
}
