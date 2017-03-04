package importnew.importnewclient.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by Xingfeng on 2016/6/7.
 */
public class DisplayUtil {

    /**
     * dp to sp
     *
     * @param dp
     * @return
     */
    public static int dp2sp(Context context, float dp) {

        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);

    }

    public static int screenWidth(Context mContext) {

        WindowManager wm = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        Point outSize = new Point();
        wm.getDefaultDisplay().getSize(outSize);
        return outSize.x;


    }
}
