package importnew.importnewclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import importnew.importnewclient.net.HttpManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 三级缓存，用于缓存图片。
 * 内存+硬盘+网络
 * Created by Xingfeng on 2016/5/7.
 */
public class ThridCache {

    private static ThridCache instance;

    public static ThridCache getInstance(Context context) {

        if (instance == null) {
            synchronized (ThridCache.class) {
                if (instance == null)
                    instance = new ThridCache(context);
            }
        }


        return instance;
    }

    //内存缓存
    private LruCache<String, Bitmap> mLruCache;
    //硬盘缓存
    private DiskLruCache mDiskLruCache;
    //网络缓存
    private OkHttpClient httpClient;

    private ThridCache(Context context) {

        //内存缓存
        int size = (int) (Runtime.getRuntime().maxMemory() / 8);
        mLruCache = new LruCache<String, Bitmap>(size) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        //硬盘缓存
        File cacheFile = AppUtil.getDiskCacheDir(context, "thumb");
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }

        try {
            mDiskLruCache = DiskLruCache.open(cacheFile, AppUtil.getAppVersion(context), 1, 20 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //网络缓存
        httpClient = HttpManager.getInstance(context).getClient();

    }


    /**
     * 将图片添加到缓存
     *
     * @param url
     * @param bitmap
     */
    private void addBitmapToMemory(String url, Bitmap bitmap) {
        if (mLruCache.get(url) == null)
            mLruCache.put(url, bitmap);
    }

    /**
     * 将图片从缓存中取出
     *
     * @param url
     * @return 缓存中没有返回null
     */
    public Bitmap getBitmapFromMemory(String url) {
        return mLruCache.get(url);
    }


    /**
     * 从硬盘中获取图片，如果硬盘中有，则将图片添加到缓存中
     *
     * @param imgeUrl
     * @return 硬盘中没有缓存则返回null
     */
    public Bitmap getBitmapFromDiskCache(String imgeUrl) {

        DiskLruCache.Snapshot snapshot = getCache(imgeUrl);
        if (snapshot != null) {
            FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(0);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            if (bitmap != null)
                addBitmapToMemory(imgeUrl, bitmap);
            return bitmap;
        } else
            return null;

    }

    /**
     * 从网络上下载图片，下载完后并将图片添加到硬盘缓存和内存缓存中
     *
     * @return 返回null表示下载失败
     */
    public Bitmap getBitmapFromNetwork(String imageUrl) {

        FileInputStream fileInputStream = null;

        DiskLruCache.Snapshot snapshot = null;

        Response response = null;

        try {
            Request request = new Request.Builder().url(imageUrl).build();
            response = httpClient.newCall(request).execute();
            InputStream inputStream = response.body().byteStream();
            //添加到硬盘缓存
            putCache(imageUrl, inputStream);
            snapshot = getCache(imageUrl);
            if (snapshot != null) {
                fileInputStream = (FileInputStream) snapshot.getInputStream(0);
            }

            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            //添加到内存缓存
            if (bitmap != null)
                addBitmapToMemory(imageUrl, bitmap);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null)
                response.body().close();
        }

        return null;

    }

    /**
     * 将内容写进缓存,耗时操作，没有开启线程
     *
     * @param url 文件URL
     * @param is  文件内容输入流
     */
    private void putCache(String url, InputStream is) {

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
    public void flushCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            return cacheKey;
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


    private DiskLruCache.Snapshot getCache(String url) {
        try {
            return mDiskLruCache.get(hashKeyForDisk(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
