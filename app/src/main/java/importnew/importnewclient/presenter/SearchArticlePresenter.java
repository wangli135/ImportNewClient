package importnew.importnewclient.presenter;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.adapter.ArticleAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.net.URLManager;
import importnew.importnewclient.parser.ArticlesParser;
import importnew.importnewclient.parser.CategoryParser;
import importnew.importnewclient.ui.ArticleContentActivity;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.utils.SecondCache;
import importnew.importnewclient.view.IArticieListView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Xingfeng on 2017-02-15.
 */

public class SearchArticlePresenter {

    private IArticieListView mArticleListView;
    private SecondCache mSecondCache;

    private int pageNum;//查询页数
    private String query = "";//查询关键字

    private List<Article> mArticles;//查询文章列表
    private ArticleAdapter mAdapter;
    private int selection;

    private Context mContext;

    public SearchArticlePresenter(IArticieListView mArticleListView, ListView listView) {
        this.mArticleListView = mArticleListView;
        this.mContext = (Context) mArticleListView;
        mSecondCache = new SecondCache((Activity) mArticleListView);
        mArticles = new ArrayList<>();
        mAdapter = new ArticleAdapter(mContext, mArticles, listView);
        mArticleListView.setAdapter(mAdapter);

    }

    public void handle(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }
        pageNum = 1;
    }

    /**
     * 搜索结果
     */
    public void queryArticles() {
        if (mArticleListView.isLoading())
            return;
        mArticleListView.setLoading(true);
        parserArticles().onBackpressureBuffer().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Article>>() {
                    @Override
                    public void onCompleted() {

                        mArticleListView.setRefreshing(false);
                        mArticleListView.setLoading(false);
                        mAdapter.notifyDataSetChanged();
                        mArticleListView.setSelection(selection);
                        pageNum++;
                    }

                    @Override
                    public void onError(Throwable e) {

                        mArticleListView.setRefreshing(false);
                        if (!mArticleListView.isLoading()) {
                            mArticleListView.showErrorInfo("加载内容发生错误，请重试");
                        } else {
                            mArticleListView.setNoContentToLoad();
                        }
                        mArticleListView.setLoading(false);

                    }

                    @Override
                    public void onNext(List<Article> articleList) {
                        selection = mArticles.size();
                        if (mArticles.isEmpty()) {
                            mArticles.addAll(articleList);
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

                String html = mSecondCache.getResponseString(url);
                if (TextUtils.isEmpty(html))
                    subscriber.onError(new Exception("加载页面无法解析"));
                else {
                    ArticlesParser categoryParser = new CategoryParser();
                    List<Article> articles = categoryParser.parser(html);
                    subscriber.onNext(articles);
                    subscriber.onCompleted();
                }

            }
        });

    }

    /**
     * 显示一篇文章的详细内容
     *
     * @param postion
     */
    public void showArticleBody(int postion) {
        Article article = mArticles.get(postion);
        Intent intent = new Intent(mContext, ArticleContentActivity.class);
        intent.putExtra(Constants.Key.ARTICLE, article);
        mContext.startActivity(intent);

    }

    public void cancelAllTasks() {
        mAdapter.cancelAllTasks();
    }
}
