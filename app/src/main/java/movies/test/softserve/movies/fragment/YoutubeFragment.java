package movies.test.softserve.movies.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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

    private static String VIDEO_ID = "VIDEO_ID";

    private String videoId = "EGy39OMyHzw";
    public static YoutubeFragment newInstance(String videoId) {

        Bundle args = new Bundle();
        args.putString(VIDEO_ID,videoId);

        YoutubeFragment fragment = new YoutubeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getString(VIDEO_ID)!=null){
            videoId = getArguments().getString(VIDEO_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_youtube, container, false);

        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_layout, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(API_KEY, new OnInitializedListener() {

            @Override
            public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    player.loadVideo(videoId);
                    player.play();
                }
            }

            @Override
            public void onInitializationFailure(Provider provider, YouTubeInitializationResult error) {
                String errorMessage = error.toString();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                Log.d("errorMessage:", errorMessage);
            }
        });

        return rootView;
    }
}