package importnew.importnewclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;

import importnew.importnewclient.R;
import importnew.importnewclient.customview.LoadMoreListView;
import importnew.importnewclient.presenter.SearchArticlePresenter;
import importnew.importnewclient.view.IArticieListView;

public class SearchResultActivity extends AppCompatActivity implements IArticieListView {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LoadMoreListView mLoadMoreLv;
    private boolean isLoading;

    private SearchArticlePresenter mSearchArticlePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initViews();

        mSearchArticlePresenter = new SearchArticlePresenter(this, mLoadMoreLv);
        mSearchArticlePresenter.handle(getIntent());
        mSearchArticlePresenter.queryArticles();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchArticlePresenter != null)
            mSearchArticlePresenter.cancelAllTasks();
    }

    private void initViews() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.search_swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mLoadMoreLv = (LoadMoreListView) findViewById(R.id.search_result_lv);
        mLoadMoreLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchArticlePresenter.showArticleBody(position);
            }
        });
        mLoadMoreLv.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoad() {
                mSearchArticlePresenter.queryArticles();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mSearchArticlePresenter.handle(intent);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mLoadMoreLv.setAdapter(adapter);
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void setNoContentToLoad() {
        mLoadMoreLv.setNoContentToLoad();
    }

    @Override
    public void setSelection(int selection) {
        mLoadMoreLv.setSelection(selection);
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
