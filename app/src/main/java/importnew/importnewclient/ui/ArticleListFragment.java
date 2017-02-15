package importnew.importnewclient.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import importnew.importnewclient.R;
import importnew.importnewclient.customview.LoadMoreListView;
import importnew.importnewclient.presenter.ArticleListPresenter;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.view.IArticieListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends BaseFragment implements ListView.OnItemClickListener, IArticieListView {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LoadMoreListView mListView;
    private boolean isLoading;//加载更多的标志

    /**
     * 文章分类URL
     */
    private String category;

    /**
     * 文章页数
     */
    private int pageNum = 1;

    private int selection;//ListView选中Item位置

    private ArticleListPresenter mArticleListPresenter;

    public ArticleListFragment() {
    }

    private BroadcastReceiver myReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            category = (String) savedInstanceState.get(Constants.Key.ARTICLE_BASE_URL);
            selection = (Integer) savedInstanceState.get(Constants.Key.SELECTION);
            pageNum = savedInstanceState.getInt(Constants.Key.PAGE_NUM);
        } else {
            pageNum = 1;
            selection = 0;
        }
        IntentFilter intentFilter = new IntentFilter("com.importnew.listview.selection");
        myReceiver = new ListViewSelectionReceiver();
        mContext.registerReceiver(myReceiver, intentFilter);

    }

    public static ArticleListFragment newInstance(String baseurl) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Key.ARTICLE_BASE_URL, baseurl);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.article_swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String url = category + 1;
                mArticleListPresenter.refreshArticles(url);
            }
        });

        mListView = (LoadMoreListView) view.findViewById(R.id.articles_lv);
        mListView.setOnItemClickListener(this);
        mListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoad() {

                isLoading = true;
                loadArticles();

            }
        });

        mArticleListPresenter = new ArticleListPresenter(this, mListView);
        mListView.setSelection(selection);
        mSwipeRefreshLayout.setRefreshing(true);
        loadArticles();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
        if (mArticleListPresenter != null)
            mArticleListPresenter.cancelAllTasks();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
        if (myReceiver != null)
            mContext.unregisterReceiver(myReceiver);
        if (mArticleListPresenter != null)
            mArticleListPresenter.cancelAllTasks();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.Key.ARTICLE_BASE_URL, category);
        outState.putInt(Constants.Key.SELECTION, mListView.getSelectedItemPosition());
        outState.putInt(Constants.Key.PAGE_NUM, pageNum);
        super.onSaveInstanceState(outState);

    }

    private void loadArticles() {
        category = (String) getArguments().get(Constants.Key.ARTICLE_BASE_URL);
        String url = category + (pageNum++);
        mArticleListPresenter.loadArticles(url);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mArticleListPresenter.showArticleBody(position);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void setNoContentToLoad() {
        mListView.setNoContentToLoad();
    }

    @Override
    public void setSelection(int selection) {
        mListView.setSelection(selection);
    }

    @Override
    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    @Override
    public void setRefreshing(boolean isRefresh) {
        mSwipeRefreshLayout.setRefreshing(isRefresh);
    }

    @Override
    public void showErrorInfo(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }


    class ListViewSelectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("com.importnew.listview.selection") && mListView != null) {
                mListView.setSelection(0);
            }

        }
    }

}
