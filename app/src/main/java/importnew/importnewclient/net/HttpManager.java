package importnew.importnewclient.net;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * HTTP连接管理，单例，全局只提供一个OkhttpClient对象
 * Created by Xingfeng on 2016/5/7.
 */
public class HttpManager {

    private static HttpManager instance = null;

    public static HttpManager getInstance(Context context) {

        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null) {
                    instance = new HttpManager(context);
                }
            }
        }
        return instance;

    }


    private OkHttpClient client;

    private HttpManager(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).addInterceptor(new ArticleBodyInterceptor()).retryOnConnectionFailure(true);
        File cacheFile = getDiskCacheDir(context, "response");
        if (!cacheFile.exists()) {
            cacheFile.exists();
        }
        Cache cache = new Cache(cacheFile, 20 * 1024 * 1024);//20M响应缓存
        client = builder.cache(cache).build();
    }

    public OkHttpClient getClient() {
        return client;
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
}
