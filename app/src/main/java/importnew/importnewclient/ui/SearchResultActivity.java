package importnew.importnewclient.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.net.URLManager;
import importnew.importnewclient.parser.ArticlesParser;
import importnew.importnewclient.utils.SecondCache;
import importnew.importnewclient.view.LoadMoreListView;

public class SearchResultActivity extends AppCompatActivity {

    /**
     * 查询的关键字
     */
    private String query;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LoadMoreListView mLoadMoreLv;
    private List<Article> mArticles;
    private ArticleAdapter mAdapter;
    private SecondCache mSecondCache;

    private SearchTask mSearchTask;
    /**
     * 查询页数
     */
    private int pageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSecondCache = SecondCache.getInstance(this);

        handle(getIntent());

        initViews();

        searchResults();
    }

    private void searchResults() {
        mSearchTask = new SearchTask();
        mSearchTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSearchTask != null)
            mSearchTask.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSecondCache != null)
            mSecondCache.cancelCalls();
    }

    private void initViews() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.search_swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setRefreshing(true);

        mLoadMoreLv = (LoadMoreListView) findViewById(R.id.search_result_lv);
        mArticles = new ArrayList<>();
        mAdapter = new ArticleAdapter(this, mArticles);
        mLoadMoreLv.setAdapter(mAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handle(intent);
    }

    private void handle(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }

    }

    class SearchTask extends AsyncTask<Void, Void, List<Article>> {

        @Override
        protected List<Article> doInBackground(Void... params) {

            String url = URLManager.HOMEPAGE + "/page/" + pageNum++ + "?s=" + query;
            String html = mSecondCache.getResponseFromDiskCache(url);
            if (TextUtils.isEmpty(html))
                html = mSecondCache.getResponseFromNetwork(url);

            if (!TextUtils.isEmpty(html))
                return ArticlesParser.parserArtciles(html);
            return null;
        }

        @Override
        protected void onPostExecute(List<Article> articles) {
            super.onPostExecute(articles);
            if (articles != null) {

                mSwipeRefreshLayout.setEnabled(false);
                mLoadMoreLv.setSelection(mArticles.size());
                for (Article article : articles) {
                    if (!mArticles.contains(article)) {
                        mArticles.add(article);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
