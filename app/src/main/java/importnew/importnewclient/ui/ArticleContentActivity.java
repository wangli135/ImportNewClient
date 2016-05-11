package importnew.importnewclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleBodyAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.bean.ArticleBody;
import importnew.importnewclient.parser.ArticleBodyParser;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.utils.SecondCache;

/**
 * 显示文章详情的页面
 */
public class ArticleContentActivity extends AppCompatActivity {

    private Article mArticle;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private ArticleBodyAdapter mAdapter;

    private SecondCache mSecondCache;

    /**
     * 是否收藏
     */
    private boolean isFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content);
        mArticle = (Article) getIntent().getSerializableExtra(Constants.Key.ARTICLE);
        isFavourite = mArticle.isFavourite();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSecondCache = SecondCache.getInstance(this);

        initViews();

    }

    private void initViews() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.article_swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mListView = (ListView) findViewById(R.id.article_content_lv);

        new LoadAndParserWorker().execute();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null)
            mAdapter.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null)
            mAdapter.cancelAllTasks();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        mArticle.setFavourite(isFavourite);
        intent.putExtra(Constants.Key.ARTICLE, mArticle);
        setResult(Activity.RESULT_OK, intent);

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_article_content_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_favourite);
        if (isFavourite)
            menuItem.setIcon(R.drawable.ic_menu_favorite_red);
        else
            menuItem.setIcon(R.drawable.ic_menu_favorite_white);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_favourite: {

                isFavourite = !isFavourite;
                if (isFavourite) {
                    item.setIcon(R.drawable.ic_menu_favorite_red);
                    Toast.makeText(this, R.string.favourite_success, Toast.LENGTH_SHORT).show();
                } else {
                    item.setIcon(R.drawable.ic_menu_favorite_white);
                    Toast.makeText(this, R.string.cancel_favourite, Toast.LENGTH_SHORT).show();
                }

            }
            break;
            case R.id.menu_share:
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    class LoadAndParserWorker extends AsyncTask<Void, Void, ArticleBody> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected ArticleBody doInBackground(Void... params) {

            String html = mArticle.getBodyString();

            if (TextUtils.isEmpty(html)) {
                html = mSecondCache.getResponseFromDiskCache(mArticle.getUrl());
                if (TextUtils.isEmpty(html))
                    html = mSecondCache.getResponseFromNetwork(mArticle.getUrl());
            }

            if (!TextUtils.isEmpty(html)) {

                if (mArticle.getBodyString() == null)
                    mArticle.setBodyString(html);

                return ArticleBodyParser.parser(html);
            }

            return null;

        }

        @Override
        protected void onPostExecute(ArticleBody articleBody) {
            super.onPostExecute(articleBody);
            mSwipeRefreshLayout.setRefreshing(false);
            if (articleBody != null) {
                mAdapter = new ArticleBodyAdapter(ArticleContentActivity.this, articleBody);
                mListView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setEnabled(false);
            } else {
                mListView.setEmptyView(null);
            }
        }
    }
}
