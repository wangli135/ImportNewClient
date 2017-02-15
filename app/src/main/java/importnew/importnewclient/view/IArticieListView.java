package importnew.importnewclient.view;


import android.widget.ListAdapter;

/**
 * 分类文章列表Fragment或Activity对应的View
 * Created by Xingfeng on 2017-02-14.
 */
public interface IArticieListView extends IBaseView {

    void setAdapter(ListAdapter adapter);

    /**
     * 判断是否当前处于加载阶段
     *
     * @return true表示正在加载
     */
    boolean isLoading();

    /**
     * 设置没有更多文章加载
     */
    void setNoContentToLoad();

    /**
     * ListView设置选中Item位置
     *
     * @param selection
     */
    void setSelection(int selection);

    void setLoading(boolean isLoading);

}
