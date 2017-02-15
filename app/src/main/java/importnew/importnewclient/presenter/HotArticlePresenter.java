package importnew.importnewclient.presenter;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.adapter.HotArticleAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.net.URLManager;
import importnew.importnewclient.utils.SecondCache;
import importnew.importnewclient.view.IHotArticleView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Xingfeng on 2017-02-15.
 */

public abstract class HotArticlePresenter {

    private IHotArticleView mHotArticleView;
    private SecondCache mSecondCache;
    private List<Article> mArticles;
    private HotArticleAdapter mAdapter;

    public HotArticlePresenter(IHotArticleView mHotArticleView) {
        this.mHotArticleView = mHotArticleView;
        Fragment fragment = (Fragment) mHotArticleView;
        mSecondCache = new SecondCache(fragment.getActivity());

        mArticles = new ArrayList<>();
        mAdapter = new HotArticleAdapter(fragment.getActivity(), mArticles);
        mHotArticleView.setAdapter(mAdapter);

    }

    public void loadHotArticles(final boolean isRefresh) {

        mHotArticleView.setRefreshing(true);
        mHotArticleView.setRecycleViewVisibility(View.INVISIBLE);
        Observable<List<Article>> observable = Observable.create(new Observable.OnSubscribe<List<Article>>() {

            @Override
            public void call(Subscriber<? super List<Article>> subscriber) {
                String url = URLManager.HOMEPAGE;
                String html = "";
                if (isRefresh)
                    html = mSecondCache.getResponseFromNetwork(url);

                if (TextUtils.isEmpty(html))
                    html = mSecondCache.getResponseFromNetwork(url);

                if (!TextUtils.isEmpty(html)) {
                    List<Article> articles = parserHotArticles(html);
                    subscriber.onNext(articles);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Exception("加载文章列表发生异常"));
                }
            }
        });

        observable.onBackpressureBuffer().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Article>>() {

            @Override
            public void onCompleted() {
                mHotArticleView.setRefreshing(false);
                mHotArticleView.setRecycleViewVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                mHotArticleView.setRefreshing(false);
                mHotArticleView.showErrorInfo("加载文章列表发生错误，请重新刷新");
            }

            @Override
            public void onNext(List<Article> articleList) {

                mArticles.clear();
                mArticles.addAll(articleList);
            }
        });
    }

    public abstract List<Article> parserHotArticles(String html);

}
