package importnew.importnewclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import importnew.importnewclient.R;
import importnew.importnewclient.net.HttpManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 图片加载工具类
 * Created by Xingfeng on 2016/5/23.
 */
public class ImageLoader {

    private static final String TAG = "ImageLoader";
    public static final int MESSAGE_POST_RESULT = 1;
    private static final int TAG_KEY_URI = R.id.article_img;

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
            LoaderResult result = (LoaderResult) msg.obj;
            ImageView imageView = result.imageView;
            String uri = (String) imageView.getTag(TAG_KEY_URI);
            if (uri.equals(result.uri)) {
                imageView.setImageBitmap(result.bitmap);
            } else {
                Log.w(TAG, "set image bitmap,but url has changed, ignored!");
            }
        }

    };

    private ImageResizer mImageResizer = new ImageResizer();
    private volatile static ImageLoader instance = null;

    private LruCache<String, Bitmap> mMemoryCache;
    private OkHttpClient mHttpClient;

    /**
     * 关联的ListView，用于寻找ImageView
     */
    private ListView mListView;

    public void setListView(ListView mListView) {
        this.mListView = mListView;
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

        imageResizer = new ImageResizer();

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
     * load bitmap from memory cache or disk cache or network async, then bind imageView and bitmap.
     * NOTE THAT: should run in UI Thread
     *
     * @param uri http url
     */
    public void bindBitmap(final String uri) {
        bindBitmap(uri, 0, 0);
    }

    public void bindBitmap(final String uri,
                           final int reqWidth, final int reqHeight) {
        final ImageView imageView = (ImageView) mListView.findViewWithTag(uri);

        if (imageView == null) {
            Log.d(TAG, "Cannot find imageview");
            return;
        }

        Bitmap bitmap = getBitmapFromMemory(uri);
        if (bitmap != null && imageView != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        Runnable loadBitmapTask = new Runnable() {

            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(uri, reqWidth, reqHeight);
                if (bitmap != null) {
                    LoaderResult result = new LoaderResult(imageView, uri, bitmap);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT, result).sendToTarget();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    public Bitmap loadBitmap(String url, int reqWidth, int reqHeight) {

        Bitmap bitmap = getBitmapFromMemory(url);
        if (bitmap != null) {
            Log.d(TAG, "loadBitmapFromMemCache,url:" + url);
            return bitmap;
        }

        Response response = null;
        try {

            Request request = new Request.Builder().url(url).build();
            response = mHttpClient.newCall(request).execute();
            bitmap = mImageResizer.decodeSampledBitmapFromInputStream(response.body().byteStream(), reqWidth, reqHeight);
            if (bitmap != null) {
                addBitmapToMemory(url, bitmap);
                Log.d(TAG, "loadBitmapFromOkHttp,url:" + url);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null)
                response.body().close();
        }

        return bitmap;


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


    private ImageResizer imageResizer;

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
            if (response.isSuccessful())
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

    private static class LoaderResult {
        public ImageView imageView;
        public String uri;
        public Bitmap bitmap;

        public LoaderResult(ImageView imageView, String uri, Bitmap bitmap) {
            this.imageView = imageView;
            this.uri = uri;
            this.bitmap = bitmap;
        }
    }
}
