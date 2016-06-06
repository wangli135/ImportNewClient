package importnew.importnewclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

import importnew.importnewclient.R;
import importnew.importnewclient.net.UpdateUtil;
import importnew.importnewclient.utils.Constants;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

//        findViewById(android.R.id.content).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                checkUpdate();
//            }
//        }, 1000);

        checkUpdate();

    }

    private void checkUpdate() {

        Observable<UpdateUtil.AppUpdateInfo> observable = Observable.create(new Observable.OnSubscribe<UpdateUtil.AppUpdateInfo>() {
            @Override
            public void call(Subscriber<? super UpdateUtil.AppUpdateInfo> subscriber) {

                UpdateUtil.AppUpdateInfo appUpdateInfo = new UpdateUtil().checkUpdate(SplashActivity.this);
                if (appUpdateInfo == null)
                    subscriber.onError(new NullPointerException("AppUpdateInfo为空"));

                subscriber.onNext(appUpdateInfo);
                subscriber.onCompleted();
            }
        }).delay(1, TimeUnit.SECONDS);
        observable.onBackpressureBuffer().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<UpdateUtil.AppUpdateInfo>() {

            UpdateUtil.AppUpdateInfo info = null;

            @Override
            public void onCompleted() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra(Constants.Key.UPDATE_INFO, info);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onNext(UpdateUtil.AppUpdateInfo appUpdateInfo) {
                info = appUpdateInfo;
            }
        });
    }
}
