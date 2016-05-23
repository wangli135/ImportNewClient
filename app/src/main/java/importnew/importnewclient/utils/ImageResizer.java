package importnew.importnewclient.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;
import java.io.InputStream;

/**
 * Bitmap工具类，用于缩放Bitmap
 * Created by Xingfeng on 2016/5/3.
 */
public class ImageResizer {

    /**
     * 求缩放比例
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){

        final int height=options.outHeight;
        final int width=options.outHeight;
        int inSampleSize=1;

        if(height>reqHeight||width>reqWidth){

            final int heightRadio=height/reqHeight;
            final int widthRadio=width/reqWidth;
            inSampleSize=Math.max(heightRadio,widthRadio);

        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,int resId,int reqWidth,int reqHeight){

        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeResource(res,resId,options);
        options.inSampleSize=calculateInSampleSize(options,reqWidth,reqHeight);

        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeResource(res,resId,options);

    }

    public static Bitmap decodeSampledBitmapFromInputStream(InputStream in,int reqWidth, int reqHieght){

        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeStream(in,null,options);
        options.inSampleSize=calculateInSampleSize(options,reqWidth,reqHieght);

        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeStream(in,null,options);
    }

    public static Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHieght){

        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFileDescriptor(fd,null,options);
        options.inSampleSize=calculateInSampleSize(options,reqWidth,reqHieght);

        options.inJustDecodeBounds=false;
        return  BitmapFactory.decodeFileDescriptor(fd,null,options);
    }
}
