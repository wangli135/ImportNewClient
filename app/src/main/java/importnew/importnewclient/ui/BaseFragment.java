package importnew.importnewclient.ui;

import android.content.Context;
import android.support.v4.app.Fragment;

import importnew.importnewclient.utils.SecondCache;
import okhttp3.OkHttpClient;

/**
 * Created by Xingfeng on 2016/5/9.
 */
public class BaseFragment extends Fragment {

    protected Context mContext;
    protected SecondCache mSecondCache;
    protected OkHttpClient httpClient;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
        mSecondCache=SecondCache.getInstance(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mSecondCache!=null)
            mSecondCache.flushCache();
    }
}
