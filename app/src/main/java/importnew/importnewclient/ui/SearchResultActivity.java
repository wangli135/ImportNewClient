package importnew.importnewclient.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.net.URLManager;
import importnew.importnewclient.parser.ArticlesParser;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.utils.SecondCache;
import importnew.importnewclient.view.LoadMoreListView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    /**
     * 查询页数
     */
    private int pageNum = 1;

    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSecondCache = SecondCache.getInstance(this);

        handle(getIntent());

        initViews();

        loadResults();
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

        mLoadMoreLv = (LoadMoreListView) findViewById(R.id.search_result_lv);
        mLoadMoreLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Article article = mArticles.get(position);
                Intent intent = new Intent(SearchResultActivity.this, ArticleContentActivity.class);
                intent.putExtra(Constants.Key.ARTICLE, article);
                startActivity(intent);

            }
        });
        mLoadMoreLv.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoad() {
                isLoading = true;
                pageNum++;
                loadResults();
            }
        });
        mArticles = new ArrayList<>();
        mAdapter = new ArticleAdapter(this, mArticles, mLoadMoreLv);
        mLoadMoreLv.setAdapter(mAdapter);
        mSwipeRefreshLayout.setRefreshing(true);


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
        handle(intent);
    }

    private void handle(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }

    }

    private void loadResults() {

        parserArticles().onBackpressureBuffer().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Article>>() {
                    @Override
                    public void onCompleted() {

                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onError(Throwable e) {

                        mSwipeRefreshLayout.setRefreshing(false);
                        if (!isLoading)
                            Toast.makeText(SearchResultActivity.this, "加载内容发生错误，请重试", Toast.LENGTH_SHORT).show();
                        else {
                            mLoadMoreLv.setNoContentToLoad();
                            mSwipeRefreshLayout.setEnabled(false);
                        }

                    }

                    @Override
                    public void onNext(List<Article> articleList) {

                        mLoadMoreLv.setSelection(mArticles.size() - 1);
                        if (mArticles.isEmpty()) {
                            mArticles.addAll(articleList);
                            mSwipeRefreshLayout.setEnabled(false);
                        } else {
                            for (Article article : articleList) {
                                if (!mArticles.contains(article)) {
                                    mArticles.add(article);
                                }
                            }
                        }

                    }
                });
    }

    private Observable<List<Article>> parserArticles() {

        return Observable.create(new Observable.OnSubscribe<List<Article>>() {
            @Override
            public void call(Subscriber<? super List<Article>> subscriber) {

                String url = URLManager.HOMEPAGE + "/page/" + pageNum++ + "?s=" + query;

                String html = mSecondCache.getResponseFromDiskCache(url);
                if (TextUtils.isEmpty(html))
                    html = mSecondCache.getResponseFromNetwork(url);

                if (TextUtils.isEmpty(html))
                    subscriber.onError(new Exception("加载页面无法解析"));
                else {
                    List<Article> articles = ArticlesParser.parserArtciles(html);
                    subscriber.onNext(articles);
                    subscriber.onCompleted();
                }

            }
        });

    }

}
