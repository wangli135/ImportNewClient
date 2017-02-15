package importnew.importnewclient.view;


import android.support.v7.widget.RecyclerView;

/**
 * Created by Xingfeng on 2017-02-15.
 */

public interface IHotArticleView extends IBaseView {

    /**
     * 设置RecycleView的可见性
     *
     * @param visible
     */
    void setRecycleViewVisibility(int visible);

    /**
     * 设置RecycleView的Adapter
     *
     * @param adapter
     */
    void setAdapter(RecyclerView.Adapter adapter);

}
