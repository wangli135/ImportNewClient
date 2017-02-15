package importnew.importnewclient.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import importnew.importnewclient.R;

/**
 * Created by Xingfeng on 2016/5/10.
 */
public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {

    private RelativeLayout footView;
    private OnLoadMoreListener onLoadMoreListener;
    private OnScrollListener onScrollListener;
    private int lastVisibleItem;//最后一个可见的Item


    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public LoadMoreListView(Context context) {
        this(context, null);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    private void init(Context context) {
        footView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.listview_foot_view, null);
        addFooterView(footView);

        footView.setVisibility(GONE);

        super.setOnScrollListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (onScrollListener != null)
            onScrollListener.onScrollStateChanged(view, scrollState);

    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (visibleItemCount == totalItemCount) {
            dismissFootView();
            return;
        }

        lastVisibleItem = firstVisibleItem + visibleItemCount;


        if (lastVisibleItem >= totalItemCount) {

            showFootView();

            if (onLoadMoreListener != null) {
                onLoadMoreListener.onLoad();
            }

        } else {
            dismissFootView();
        }


    }

    /**
     * 没有内容加载了，移除FootView
     */
    public void setNoContentToLoad() {
        removeFooterView(footView);
        footView = null;
    }

    public void showFootView() {
        if (footView != null)
            footView.setVisibility(VISIBLE);
    }

    public void dismissFootView() {
        if (footView != null)
            footView.setVisibility(GONE);
    }

    public interface OnLoadMoreListener {
        void onLoad();
    }
}
