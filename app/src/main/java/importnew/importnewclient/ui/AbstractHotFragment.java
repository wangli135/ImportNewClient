package importnew.importnewclient.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import importnew.importnewclient.R;
import importnew.importnewclient.presenter.HotArticlePresenter;
import importnew.importnewclient.view.IHotArticleView;

/**
 * 年度和月热门文章Fragment的父类
 * Created by Xingfeng on 2017-02-15.
 */
public abstract class AbstractHotFragment extends BaseFragment implements IHotArticleView {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecycleView;

    private LinearLayoutManager layoutManager;

    private HotArticlePresenter mHotArticlePresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_most_discussed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.most_discussed_swiperefreshview);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHotArticlePresenter.loadHotArticles(true);
            }
        });

        mRecycleView = (RecyclerView) view.findViewById(R.id.most_discussed_recycleview);
        mRecycleView.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(layoutManager);

        mHotArticlePresenter = getHotArticlePresenter();
        mHotArticlePresenter.loadHotArticles(false);

    }

    public abstract HotArticlePresenter getHotArticlePresenter();

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecycleView.setAdapter(adapter);
    }

    @Override
    public void setRecycleViewVisibility(int visible) {
        mRecycleView.setVisibility(visible);
    }

    @Override
    public void setRefreshing(boolean isRefresh) {
        mSwipeRefreshLayout.setRefreshing(isRefresh);
    }

    @Override
    public void showErrorInfo(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
