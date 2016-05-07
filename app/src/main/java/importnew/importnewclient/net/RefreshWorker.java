package importnew.importnewclient.net;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 刷新工具类
 * Created by Xingfeng on 2016/5/7.
 */
public class RefreshWorker{

    public interface OnRefreshListener{
        void onRefresh(String html);
    }

    private OnRefreshListener onRefreshListener;
    private OkHttpClient httpClient;
    private String url;


    public RefreshWorker(OnRefreshListener onRefreshListener, OkHttpClient httpClient, String url) {
        this.onRefreshListener = onRefreshListener;
        this.httpClient = httpClient;
        this.url = url;

        new RefershAsyncTask().execute(url);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    class RefershAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                String urlStr=params[0];
                Request request=new Request.Builder().url(urlStr).cacheControl(new CacheControl.Builder().noCache().build())
                        .build();
                Response response=httpClient.newCall(request).execute();
                if(response.isSuccessful()){
                    return response.body().string();
                }
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(onRefreshListener!=null)
                onRefreshListener.onRefresh(s);
        }
    }

}
