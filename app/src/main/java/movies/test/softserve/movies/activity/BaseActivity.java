package movies.test.softserve.movies.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import movies.test.softserve.movies.controller.MainController;

/**
 * Created by rkrit on 30.11.17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainController.getInstance().addDbObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainController.getInstance().setCurrentContext(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainController.getInstance().removeDbObserver(this);
    }
}
