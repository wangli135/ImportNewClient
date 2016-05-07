package importnew.importnewclient.utils;

import android.content.Context;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import importnew.importnewclient.net.HttpManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 两级缓存，用于所有的网页请求
 * 网络+硬盘缓存
 * Created by Xingfeng on 2016/5/7.
 */
public class SecondCache {

    //硬盘缓存
    private DiskLruCache mDiskLruCache;
    //网络缓存
    private OkHttpClient httpClient;


    private static SecondCache instance = null;

    public static SecondCache getInstance(Context context) {

        if (instance == null) {
            synchronized (SecondCache.class) {
                if (instance == null)
                    instance = new SecondCache(context);
            }
        }


        return instance;
    }

    private SecondCache(Context context) {

        //硬盘缓存
        File cacheFile = AppUtil.getDiskCacheDir(context, "pages");
        if (!cacheFile.exists())
            cacheFile.mkdirs();
        try {
            mDiskLruCache = DiskLruCache.open(cacheFile, AppUtil.getAppVersion(context), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //网络缓存
        httpClient = HttpManager.getInstance(context).getClient();

    }

    /**
     * 从硬盘中获取响应
     *
     * @param url
     * @return
     */
    public String getResponseFromDiskCache(String url) {

        DiskLruCache.Snapshot snapshot = getCache(url);
        if (snapshot == null)
            return null;
        else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(snapshot.getInputStream(0)));
            StringBuilder result = new StringBuilder();
            String line = "";
            try {
                while ((line = reader.readLine()) != null)
                    result.append(line);
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    /**
     * 从网络获取响应，将得到的响应写进硬盘缓存
     *
     * @param url
     * @return
     */
    public String getResponseFromNetwork(String url) {

        try {
            Request request = new Request.Builder().url(url).build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                InputStream inputStream = response.body().byteStream();
                //添加到硬盘缓存
                putCache(url, inputStream);
                return response.body().string();
            } else {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;


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

    private DiskLruCache.Snapshot getCache(String url) {
        try {
            return mDiskLruCache.get(hashKeyForDisk(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
