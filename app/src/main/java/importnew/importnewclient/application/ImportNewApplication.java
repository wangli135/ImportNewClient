package importnew.importnewclient.application;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;

import com.squareup.leakcanary.LeakCanary;

import im.fir.sdk.FIR;

/**
 * Created by Xingfeng on 2016/5/9.
 */
public class ImportNewApplication extends Application {

    /**
     * 屏幕宽度，单位像素
     */
    public static int SCREEN_WIDTH;

    /**
     * 屏幕高度，单位像素
     */
    public static int SCREEN_HEIGHT;

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FIR.init(this);
        LeakCanary.install(this);
        getWidthAndHeight();

    }

    private void getWidthAndHeight() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        SCREEN_WIDTH = metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;

    }
}
