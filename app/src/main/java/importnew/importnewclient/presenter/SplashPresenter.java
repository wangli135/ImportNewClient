package importnew.importnewclient.presenter;

import android.app.Activity;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import importnew.importnewclient.net.UpdateUtil;
import importnew.importnewclient.ui.MainActivity;
import importnew.importnewclient.utils.Constants;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Xingfeng on 2017-02-15.
 */

public class SplashPresenter {

    private Activity mActivity;

    public SplashPresenter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void checkUpdate() {
        Observable<UpdateUtil.AppUpdateInfo> observable = Observable.create(new Observable.OnSubscribe<UpdateUtil.AppUpdateInfo>() {
            @Override
            public void call(Subscriber<? super UpdateUtil.AppUpdateInfo> subscriber) {

                UpdateUtil.AppUpdateInfo appUpdateInfo = new UpdateUtil().checkUpdate(mActivity);
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
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.putExtra(Constants.Key.UPDATE_INFO, info);
                mActivity.startActivity(intent);
                mActivity.finish();
            }

            @Override
            public void onError(Throwable e) {
                Intent intent = new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
            }

            @Override
            public void onNext(UpdateUtil.AppUpdateInfo appUpdateInfo) {
                info = appUpdateInfo;
            }
        });
    }

}
