package importnew.importnewclient.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * LruCache用于缓存图片
 * Created by Xingfeng on 2016/5/3.
 */
public class MyLruCache {

    private LruCache<String,Bitmap> mLruCache;

    private static MyLruCache myLruCache=null;



    private MyLruCache(){
        int memorySize= (int) (Runtime.getRuntime().maxMemory()/8);
        mLruCache=new LruCache<String,Bitmap>(memorySize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public static MyLruCache newInstance(){

        if(myLruCache==null){
            synchronized (MyLruCache.class){
                if(myLruCache==null){
                    synchronized (MyLruCache.class){
                        myLruCache=new MyLruCache();
                    }
                }
            }

        }

        return myLruCache;
    }

    public void addBitmapToCache(String url,Bitmap bitmap){
        if(getBitmapFromCache(url)==null)
            mLruCache.put(url,bitmap);
    }

    public Bitmap getBitmapFromCache(String url){
        return mLruCache.get(url);
    }

}
