package importnew.importnewclient.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;

import importnew.importnewclient.net.HttpManager;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 两级缓存，用于所有的网页请求
 * 网络+硬盘缓存
 * Created by Xingfeng on 2016/5/7.
 */
public class SecondCache {

    //网络缓存+硬盘缓存
    private OkHttpClient httpClient;

    public SecondCache(Context context) {
        //网络缓存
        httpClient = HttpManager.getInstance(context).getClient();
    }

    /**
     * 从硬盘中获取响应
     *
     * @param url
     * @return
     */
    private String getResponseFromDiskCache(String url) {

        Response response = null;
        try {
            Request request = new Request.Builder().cacheControl(CacheControl.FORCE_CACHE).url(url).build();
            Call call = httpClient.newCall(request);
            response = call.execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }

            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (response != null) {
                response.body().close();
            }
        }

    }

    /**
     * 从网络获取响应，将得到的响应写进硬盘缓存
     *
     * @param url
     * @return
     */
    public String getResponseFromNetwork(String url) {

        Response response = null;
        try {
            Request request = new Request.Builder().url(url).cacheControl(
                    new CacheControl.Builder().noCache().build()
            ).build();
            Call call = httpClient.newCall(request);
            response = call.execute();
            if (response.isSuccessful()) {

                return response.body().string();

            } else {
                return "";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (response != null) {
                response.body().close();
            }
        }
    }

    public String getResponseString(String url) {

        //Step 1：从缓存获取响应
        String result = getResponseFromDiskCache(url);
        //Step 2: 从网络获取响应
        if (TextUtils.isEmpty(result))
            result = getResponseFromNetwork(url);
        return result;
    }

    public Response getResponse(String url) {
        try {
            Request request = new Request.Builder().url(url).cacheControl(
                    new CacheControl.Builder().noCache().build()
            ).build();
            Call call = httpClient.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                return response;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
