package importnew.importnewclient.ui;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Xingfeng on 2016/5/9.
 */
public class BaseFragment extends Fragment {

    protected Context mContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }



}
