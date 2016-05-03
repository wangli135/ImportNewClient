package importnew.importnewclient.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 缓存所有网络请求
 * Created by Xingfeng on 2016/5/3.
 */
public class MyDiskLruCache {

    private DiskLruCache mDiskLruCache;

    public MyDiskLruCache(Context context, String filename) {
        File cacheFile = getDiskCacheDir(context, filename);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }

        try {
            mDiskLruCache = DiskLruCache.open(cacheFile, getAppVersion(context), 1, 10 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public DiskLruCache.Snapshot getCache(String url) {
        try {
            return mDiskLruCache.get(hashKeyForDisk(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将内容写进缓存,耗时操作，没有开启线程
     *
     * @param url 文件URL
     * @param is  文件内容输入流
     */
    public void putCache(String url, InputStream is) {

        final String key = hashKeyForDisk(url);
        OutputStream os = null;
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            os = editor.newOutputStream(0);
            int b = 0;
            while ((b = is.read()) != -1) {
                os.write(b);
            }
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 将缓存记录同步到journal文件中
     */
    public void flushCache(){
        if(mDiskLruCache!=null){
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 根据传入的filename获取硬盘缓存的路径地址
     *
     * @param context
     * @param filename
     * @return
     */
    private File getDiskCacheDir(Context context, String filename) {

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
    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 使用MD5算法对传入的key进行加密并返回
     *
     * @param key
     * @return
     */
    private String hashKeyForDisk(String key) {
        String cacheKey = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(key.getBytes());
            cacheKey = bytesToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
