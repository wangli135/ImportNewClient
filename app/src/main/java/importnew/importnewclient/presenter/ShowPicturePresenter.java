package importnew.importnewclient.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import importnew.importnewclient.utils.SecondCache;
import importnew.importnewclient.view.IShowPictureView;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Xingfeng on 2017-02-15.
 */

public class ShowPicturePresenter {

    private String picURL;
    private SecondCache secondCache;

    private IShowPictureView mShowPictureView;

    public ShowPicturePresenter(String picURL, IShowPictureView mShowPictureView) {
        this.picURL = picURL;
        this.mShowPictureView = mShowPictureView;

        secondCache = new SecondCache((Activity) mShowPictureView);

    }

    public void showPicture() {

        if (TextUtils.isEmpty(picURL))
            return;

        Observable<Bitmap> bitmapObservable = Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Response response = null;
                try {
                    response = secondCache.getResponse(picURL);
                    if (response == null)
                        subscriber.onError(new NullPointerException("Response为null"));

                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                    if (bitmap == null)
                        subscriber.onError(new NullPointerException("Bitmap为null"));

                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(new NullPointerException("Response为null"));
                } finally {
                    if (response != null) {
                        response.body().close();
                    }
                }
            }
        });

        bitmapObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {
                        mShowPictureView.setProgressVisiblity(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mShowPictureView.setProgressVisiblity(View.INVISIBLE);
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {

                        Animation animation = new AlphaAnimation(0.0f, 1.0f);
                        animation.setDuration(1000);
                        mShowPictureView.setImageBitmap(bitmap);
                        mShowPictureView.startAnimation(animation);
                    }
                });

    }


}
