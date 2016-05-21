package importnew.importnewclient.ui;

import android.content.Context;
import android.support.v4.app.Fragment;

import importnew.importnewclient.bean.Article;
import importnew.importnewclient.utils.SecondCache;

/**
 * Created by Xingfeng on 2016/5/9.
 */
public class BaseFragment extends Fragment {

    public interface OnArticleSelectedListener{
        void onArticleSelectedListener(Article article);
    }


    protected OnArticleSelectedListener onArticleSelectedListener;
    protected Context mContext;
    protected SecondCache mSecondCache;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
        mSecondCache=SecondCache.getInstance(context);
    }

    @Override
    public void onPause() {
        super.onPause();
    }



}
