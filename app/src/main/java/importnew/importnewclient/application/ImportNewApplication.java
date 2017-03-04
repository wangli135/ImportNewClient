package importnew.importnewclient.application;

import android.app.Application;

import im.fir.sdk.FIR;

/**
 * Created by Xingfeng on 2016/5/9.
 */
public class ImportNewApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FIR.init(this);
    }

}
