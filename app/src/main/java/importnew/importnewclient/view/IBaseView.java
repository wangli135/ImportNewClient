package importnew.importnewclient.view;

/**
 * MVP结构中View的基类
 * Created by Xingfeng on 2017-02-14.
 */
public interface IBaseView {

    /**
     * 控制SwipeRefreshLayout是否显示
     *
     * @param isRefresh true表示显示，false表示不选是
     */
    void setRefreshing(boolean isRefresh);

    /**
     * Toast显示错误提示消息
     * @param message 提示消息
     */
    void showErrorInfo(String message);

}
