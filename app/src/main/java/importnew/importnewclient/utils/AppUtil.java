package importnew.importnewclient.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;

/**
 * 获取App信息的工具类
 * Created by Xingfeng on 2016/5/7.
 */
public class AppUtil {

    /**
     * 根据传入的filename获取硬盘缓存的路径地址
     *
     * @param context
     * @param filename
     * @return
     */
    public static File getDiskCacheDir(Context context, String filename) {

        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + filename);
    }

    /**
     * 获取当前应用程序的版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
