package importnew.importnewclient.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import importnew.importnewclient.R;
import importnew.importnewclient.presenter.ShowPicturePresenter;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.view.IShowPictureView;
import uk.co.senab.photoview.PhotoView;

public class ShowPictureActivity extends AppCompatActivity implements IShowPictureView {

    private PhotoView mPhotoView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);
        initViews();

        showPicture();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mPhotoView = (PhotoView) findViewById(R.id.photoView);
        mPhotoView.setMaximumScale(4);
    }

    private void showPicture() {
        String pictureUrl = getIntent().getStringExtra(Constants.Key.PICTURE_URL);
        ShowPicturePresenter showPicturePresenter = new ShowPicturePresenter(pictureUrl, this);
        showPicturePresenter.showPicture();
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        mPhotoView.setImageBitmap(bitmap);
    }

    @Override
    public void startAnimation(Animation animation) {
        mPhotoView.startAnimation(animation);
    }

    @Override
    public void setProgressVisiblity(int visiblity) {
        mProgressBar.setVisibility(visiblity);
    }
}
