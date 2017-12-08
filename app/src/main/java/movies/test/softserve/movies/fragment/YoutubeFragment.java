package movies.test.softserve.movies.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import movies.test.softserve.movies.R;

public class YoutubeFragment extends Fragment {
    private static final String API_KEY = "AIzaSyATDf1-48FCmDfqktvejCi6SPA6CQX33AM";
    private static final String TAG = "YTB";

    private static String VIDEO_ID = "VIDEO_ID";

    private String videoId = "EGy39OMyHzw";

    private int currentTimeMillis = 0;
    private YouTubePlayer youTubePlayer;

    private YouTubePlayerSupportFragment youTubePlayerFragment;

    public static YoutubeFragment newInstance(String videoId) {

        Bundle args = new Bundle();
        args.putString(VIDEO_ID, videoId);

        YoutubeFragment fragment = new YoutubeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getString(VIDEO_ID) != null) {
            videoId = getArguments().getString(VIDEO_ID);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        youTubePlayerFragment.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_youtube, container, false);

        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.youtube_layout, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(API_KEY, new OnInitializedListener() {

            @Override
            public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
                player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                player.setOnFullscreenListener(b -> {
                    if (b) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
                    } else {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                    }
                });
                player.loadVideo(videoId, currentTimeMillis);
                player.play();
                youTubePlayer = player;
            }

            @Override
            public void onInitializationFailure(Provider provider, YouTubeInitializationResult error) {
                String errorMessage = error.toString();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            currentTimeMillis = savedInstanceState.getInt(TAG, 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        currentTimeMillis = youTubePlayer.getCurrentTimeMillis();
        outState.putInt(TAG, currentTimeMillis);
    }

    public void onBackPressed() {
        youTubePlayer.setFullscreen(false);
    }
}