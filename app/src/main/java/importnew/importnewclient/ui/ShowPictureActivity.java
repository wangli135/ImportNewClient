package importnew.importnewclient.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import importnew.importnewclient.R;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.utils.SecondCache;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;

public class ShowPictureActivity extends AppCompatActivity {

    private String pictureUrl;

    private PhotoView mPhotoView;
    private ProgressBar mProgressBar;

    private SecondCache secondCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);
        initViews();

        secondCache = SecondCache.getInstance(this);

        getBitmap();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 下载文件
                Snackbar.make(view, "正在保存图片...", Snackbar.LENGTH_LONG)
                        .setAction("取消保存", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mPhotoView = (PhotoView) findViewById(R.id.photoView);
        mPhotoView.setMaximumScale(4);
    }

    private void getBitmap() {

        pictureUrl = getIntent().getStringExtra(Constants.Key.PICTURE_URL);

        if (TextUtils.isEmpty(pictureUrl))
            return;

        Observable<Bitmap> bitmapObservable = Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Response response = null;
                try {

                    response = secondCache.getResponse(pictureUrl);
                    if (response == null)
                        subscriber.onError(new NullPointerException("Response为null"));

                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                    //Bitmap bitmap = new ImageResizer().decodeSampledBitmapFromInputStream(connection.getInputStream(), mPhotoView.getWidth(), mPhotoView.getHeight());

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
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //TODO 处理图片加载出错
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {

                        Animation animation = new AlphaAnimation(0.0f, 1.0f);
                        animation.setDuration(1000);
                        mPhotoView.setImageBitmap(bitmap);
                        mPhotoView.startAnimation(animation);
                    }
                });

    }
}
