package importnew.importnewclient.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import importnew.importnewclient.R;
import importnew.importnewclient.presenter.SplashPresenter;

/**
 *
 *
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SplashPresenter splashPresenter = new SplashPresenter(this);
        splashPresenter.checkUpdate();
    }
}
