package importnew.importnewclient.utils;

import android.content.Context;

/**
 * Created by Xingfeng on 2016/6/7.
 */
public class DisplayUtil {

    /**
     * dp to sp
     * @param dp
     * @return
     */
    public static int dp2sp(Context context,float dp){

        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return  (int) (dp * scale + 0.5f);

    }

}
