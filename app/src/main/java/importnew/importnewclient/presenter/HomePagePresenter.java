package importnew.importnewclient.presenter;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.List;

import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.net.URLManager;
import importnew.importnewclient.parser.ArticlesParser;
import importnew.importnewclient.parser.HomePagerParser;
import importnew.importnewclient.utils.ArctileBlockConverter;
import importnew.importnewclient.utils.SecondCache;
import importnew.importnewclient.view.IHomePageView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Xingfeng on 2017-02-14.
 */

public class HomePagePresenter {

    private IHomePageView mHomePageView;
    private SecondCache mSecondCache;

    public HomePagePresenter(IHomePageView mHomePageView) {
        this.mHomePageView = mHomePageView;
        Fragment fragment = (Fragment) mHomePageView;
        mSecondCache = new SecondCache(fragment.getActivity());
    }


    public void getHtmlAndParser(boolean isRefresh) {
        mHomePageView.setRefreshing(true);
        getArticles(isRefresh).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleBlock>>() {
                    @Override
                    public void onCompleted() {

                        mHomePageView.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mHomePageView.setRefreshing(false);
                        mHomePageView.showErrorInfo("加载首页发生错误，请刷新重试");
                    }

                    @Override
                    public void onNext(List<ArticleBlock> articleBlocks) {

                        mHomePageView.setAdapter(articleBlocks);

                    }
                });
    }

    /**
     * 获取首页文章块
     *
     * @param isRefresh 是否刷新操作
     * @return 解析出来的文章块
     */
    private Observable<List<ArticleBlock>> getArticles(final boolean isRefresh) {

        return Observable.create(new Observable.OnSubscribe<List<ArticleBlock>>() {
            @Override
            public void call(Subscriber<? super List<ArticleBlock>> subscriber) {

                String html = "";

                if (isRefresh) {
                    html = mSecondCache.getResponseFromNetwork(URLManager.HOMEPAGE);
                }

                if (TextUtils.isEmpty(html)) {
                    html = mSecondCache.getResponseString(URLManager.HOMEPAGE);
                }

                if (TextUtils.isEmpty(html)) {
                    subscriber.onError(new NullPointerException("未解析到文章主体"));
                } else {
                    ArticlesParser homePageParser = new HomePagerParser();
                    subscriber.onNext(ArctileBlockConverter.converter(homePageParser.parser(html)));
                }

                subscriber.onCompleted();
            }
        });

    }
}
