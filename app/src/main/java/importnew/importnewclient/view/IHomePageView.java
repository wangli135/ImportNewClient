package importnew.importnewclient.view;

import android.widget.ListAdapter;

/**
 * 主页Fragment对应的View接口
 * Created by Xingfeng on 2017-02-14.
 */
public interface IHomePageView extends IBaseView {

    /**
     * 设置ListView的Adapter
     *
     * @param adapter
     */
    void setAdapter(ListAdapter adapter);

}
