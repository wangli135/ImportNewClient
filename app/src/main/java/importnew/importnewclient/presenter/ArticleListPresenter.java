package importnew.importnewclient.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.adapter.ArticleAdapter;
import importnew.importnewclient.bean.Article;
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
 * Created by Xingfeng on 2017-02-14.
 */

public class ArticleListPresenter {

    private IArticieListView mArticleListView;
    private List<Article> mArticles;
    private ArticleAdapter mAdapter;
    private SecondCache mSecondCache;
    private Context mContext;

    public ArticleListPresenter(IArticieListView mArticleListView, ListView listView) {
        this.mArticleListView = mArticleListView;
        Fragment fragment = (Fragment) mArticleListView;
        mContext = fragment.getActivity();
        mSecondCache = new SecondCache(mContext);
        mArticles = new ArrayList<>();
        mAdapter = new ArticleAdapter(mContext, mArticles, listView);
        mArticleListView.setAdapter(mAdapter);
    }

    /**
     * 根据地址加载文章列表
     *
     * @param url 文章列表地址
     */
    public void loadArticles(final String url) {

        Observable.create(new Observable.OnSubscribe<List<Article>>() {
            @Override
            public void call(Subscriber<? super List<Article>> subscriber) {

                String html = mSecondCache.getResponseString(url);

                if (TextUtils.isEmpty(html))
                    subscriber.onError(new Exception("加载页面无法解析"));
                else {
                    ArticlesParser parser = new CategoryParser();
                    List<Article> articles = parser.parser(html);
                    subscriber.onNext(articles);
                    subscriber.onCompleted();
                }

            }
        }).onBackpressureBuffer().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Article>>() {
                    @Override
                    public void onCompleted() {

                        mArticleListView.setRefreshing(false);
                        mArticleListView.setLoading(false);
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onError(Throwable e) {

                        mArticleListView.setRefreshing(false);
                        if (!mArticleListView.isLoading()) {
                            mArticleListView.showErrorInfo("加载内容发生错误，请重试");
                        } else {
                            mArticleListView.setNoContentToLoad();
                        }

                    }

                    @Override
                    public void onNext(List<Article> articleList) {

                        mArticleListView.setSelection(mArticles.size());
                        if (mArticles.isEmpty())
                            mArticles.addAll(articleList);
                        else {
                            for (Article article : articleList) {
                                if (!mArticles.get(mArticles.size() - 1).equals(article))
                                    mArticles.add(article);
                            }
                        }

                    }
                });

    }

    /**
     * 刷新文章，重新获取第一页内容
     *
     * @param url
     */
    public void refreshArticles(final String url) {

        mArticleListView.setRefreshing(true);
        Observable<List<Article>> observable = Observable.create(new Observable.OnSubscribe<List<Article>>() {
            @Override
            public void call(Subscriber<? super List<Article>> subscriber) {

                String html = mSecondCache.getResponseFromNetwork(url);

                if (TextUtils.isEmpty(html))
                    subscriber.onError(new Exception("刷新出错"));
                else {
                    ArticlesParser parser = new CategoryParser();
                    List<Article> articles = parser.parser(html);
                    subscriber.onNext(articles);
                    subscriber.onCompleted();
                }

            }
        });

        observable.onBackpressureBuffer().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Article>>() {
            @Override
            public void onCompleted() {

                mArticleListView.setRefreshing(false);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(Throwable e) {

                mArticleListView.setRefreshing(false);
                mArticleListView.showErrorInfo("加载页面出错，请重新刷新");
            }

            @Override
            public void onNext(List<Article> articles) {

                if (mArticles.isEmpty()) {
                    mArticles.addAll(articles);
                } else {
                    for (Article article : articles) {
                        if (!mArticles.get(0).equals(article))
                            mArticles.add(article);
                        else
                            break;
                    }
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
        if (mAdapter != null)
            mAdapter.cancelAllTasks();
    }
}
