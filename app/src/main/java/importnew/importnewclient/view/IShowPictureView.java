package importnew.importnewclient.view;

import android.graphics.Bitmap;
import android.view.animation.Animation;

/**
 * Created by Xingfeng on 2017-02-15.
 */

public interface IShowPictureView {

    void setImageBitmap(Bitmap bitmap);

    void startAnimation(Animation animation);

    void setProgressVisiblity(int visiblity);

}
