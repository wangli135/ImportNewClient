package importnew.importnewclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import java.io.IOException;

import importnew.importnewclient.net.HttpManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 图片加载工具类
 * Created by Xingfeng on 2016/5/23.
 */
public class ImageLoader {

    private volatile static ImageLoader instance = null;

    private LruCache<String, Bitmap> mMemoryCache;
    private OkHttpClient mHttpClient;

    private RecyclerView mRecycleView;


    public void setRecycleView(RecyclerView mRecycleView) {
        this.mRecycleView = mRecycleView;
    }

    private ImageLoader(Context context) {

        //内存缓存
        int size = (int) (Runtime.getRuntime().maxMemory() / 8);
        mMemoryCache = new LruCache<String, Bitmap>(size) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        //硬盘+网络缓存
        mHttpClient = HttpManager.getInstance(context).getClient();

    }

    public static ImageLoader getInstance(Context context) {

        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader(context);
                }
            }
        }

        return instance;
    }

    /**
     * 将图片添加进内存缓存
     *
     * @param imageUrl
     * @param bitmap
     */
    public void addBitmapToMemory(String imageUrl, Bitmap bitmap) {
        if (mMemoryCache.get(imageUrl) == null)
            mMemoryCache.put(imageUrl, bitmap);
    }

    public Bitmap getBitmapFromMemory(String imageUrl) {
        return mMemoryCache.get(imageUrl);
    }

    /**
     * 从硬盘或者网络中同步获取图片
     *
     * @param imageUrl 图片地址
     * @return
     */
    public Bitmap getBitmap(String imageUrl) {

        Bitmap bitmap = null;

        if (TextUtils.isEmpty(imageUrl))
            return bitmap;

        Response response = null;
        try {
            Request request = new Request.Builder().url(imageUrl).build();
            response = mHttpClient.newCall(request).execute();
            bitmap = BitmapFactory.decodeStream(response.body().byteStream());

            //将图片添加到内存中
            if (bitmap != null)
                addBitmapToMemory(imageUrl, bitmap);

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        } finally {
            if (response != null)
                response.body().close();
        }

    }

}
