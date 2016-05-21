package importnew.importnewclient.application;

import android.app.Application;
import android.util.DisplayMetrics;

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

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化BUGHD SDK
        FIR.init(this);
        getWidthAndHeight();

    }

    private void getWidthAndHeight() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        SCREEN_WIDTH = metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;

    }
}
