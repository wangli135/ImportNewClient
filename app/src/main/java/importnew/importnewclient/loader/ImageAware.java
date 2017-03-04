package importnew.importnewclient.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Xingfeng on 2017-03-04.
 */

public class ImageAware {

    private WeakReference<ImageView> mImageViewReference;
    private int width;
    private int height;
    private Bitmap bitmap;
    private String url;

    public ImageAware(String url, ImageView imageView, int width, int height) {
        this.url = url;
        this.mImageViewReference = new WeakReference<ImageView>(imageView);
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public ImageView getImageView() {
        return mImageViewReference.get();
    }

    public boolean isCollected() {
        return mImageViewReference.get() == null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public synchronized void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getId() {
        ImageView imageView = getImageView();
        return imageView == null ? super.hashCode() : imageView.hashCode();
    }
}
