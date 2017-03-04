package importnew.importnewclient.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import importnew.importnewclient.net.HttpManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Xingfeng on 2017-03-04.
 */

public class ImageLoader {

    private Map<Integer, String> cacheKeysForImageAwares = Collections.synchronizedMap(new HashMap<Integer, String>());

    private static final int CPU_COUNT = Runtime.getRuntime()
            .availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(128), sThreadFactory);

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            ImageAware imageAware = (ImageAware) msg.obj;
            if (imageAware.isCollected() || isViewReused(imageAware))
                return;

            ImageView imageView = imageAware.getImageView();
            Bitmap bitmap = imageAware.getBitmap();
            if (bitmap != null && imageView != null)
                imageView.setImageBitmap(bitmap);
        }

    };

    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private OkHttpClient mHttpClient;

    private ImageResizer mImageResizer = new ImageResizer();

    private volatile static ImageLoader instance = null;

    private ImageLoader(Context context) {

        this.mContext = context.getApplicationContext();

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
                    instance = new ImageLoader(context.getApplicationContext());
                }
            }
        }

        return instance;
    }

    /**
     * 判断ImageView是否被重用了
     *
     * @param imageAware
     * @return
     */
    private boolean isViewReused(ImageAware imageAware) {
        String url = cacheKeysForImageAwares.get(imageAware.getId());
        return !url.equals(imageAware.getUrl());
    }

    public void loadBitmap(final String url, final ImageAware imageAware) {

        cacheKeysForImageAwares.put(imageAware.getId(), url);

        //从内存缓存中得到key
        final String cachedKey = cachedKey(url, imageAware);
        Bitmap bitmap = getBitmapFormMemory(cachedKey);
        if (bitmap != null) {
            imageAware.setBitmap(bitmap);
            deleveryToMainThread(imageAware);
            return;
        }
        final int width = imageAware.getWidth();
        final int height = imageAware.getHeight();
        //执行网络操作（包含磁盘缓存）
        Runnable loadBitmap = new Runnable() {
            @Override
            public void run() {

                try {

                    Response response = null;
                    try {
                        Request request = new Request.Builder().url(url).build();
                        response = mHttpClient.newCall(request).execute();
                        Bitmap finalBitmap = null;
                        if (response.isSuccessful())
                            finalBitmap = mImageResizer.decodeSampledBitmapFromInputStream(new ByteArrayInputStream(response.body().bytes()), width, height);

                        //将图片添加到内存中
                        if (finalBitmap != null)
                            addBitmapToMemory(cachedKey, finalBitmap);

                        imageAware.setBitmap(finalBitmap);
                        deleveryToMainThread(imageAware);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (response != null)
                            response.body().close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmap);

    }

    private void addBitmapToMemory(String url, Bitmap bitmap) {
        synchronized (mMemoryCache) {
            mMemoryCache.put(url, bitmap);
        }
    }

    private Bitmap getBitmapFormMemory(String url) {
        synchronized (mMemoryCache) {
            return mMemoryCache.get(url);
        }
    }

    private void deleveryToMainThread(ImageAware imageAware) {
        Message message = Message.obtain();
        message.obj = imageAware;
        mMainHandler.sendMessage(message);
    }

    private String cachedKey(String url, ImageAware imageAware) {
        return url + "_" + imageAware.getWidth() + "#" + imageAware.getHeight();
    }

}
