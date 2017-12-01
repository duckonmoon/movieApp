package movies.test.softserve.movies.activity;

import android.support.v7.app.AppCompatActivity;

import movies.test.softserve.movies.controller.MainController;

/**
 * Created by rkrit on 30.11.17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        MainController.getInstance().setCurrentContext(this);
    }
}
