package importnew.importnewclient.net;

import android.os.AsyncTask;

import importnew.importnewclient.utils.SecondCache;

/**
 * 刷新工具类
 * Created by Xingfeng on 2016/5/7.
 */
public class RefreshWorker {

    public interface OnRefreshListener {
        void onRefresh(String html);
    }

    private OnRefreshListener onRefreshListener;
    private SecondCache secondCache;
    private String url;


    public RefreshWorker(OnRefreshListener onRefreshListener, SecondCache secondCache, String url) {
        this.onRefreshListener = onRefreshListener;
        this.secondCache = secondCache;
        this.url = url;

        new RefershAsyncTask().execute(url);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    class RefershAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String html = secondCache.getResponseFromNetwork(params[0]);

                if (onRefreshListener != null)
                    onRefreshListener.onRefresh(html);

                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
