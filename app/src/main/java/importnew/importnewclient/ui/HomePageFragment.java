package importnew.importnewclient.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.HomePageAdapter;
import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.net.RefreshWorker;
import importnew.importnewclient.net.URLManager;
import importnew.importnewclient.parser.HomePagerParser;
import importnew.importnewclient.utils.ArctileBlockConverter;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 首页Fragment
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends BaseFragment {


    private SwipeRefreshLayout mRefreshLayout;

    private List<ArticleBlock> articles;

    private ListView mArticleBlokcListView;
    private HomePageAdapter mHomePageAdapter;


    public HomePageFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.homepage_swiperefresh);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getHtmlAndParser(true);


            }
        });

        mArticleBlokcListView = (ListView) view.findViewById(R.id.article_block_listview);

        getHtmlAndParser(false);

    }


    @Override
    public void onPause() {
        super.onPause();

        mRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRefreshLayout.setRefreshing(false);

        if (mHomePageAdapter != null) {
            mHomePageAdapter.cancelAllTasks();
        }


    }

    /**
     * 刷新页面
     */
    private void refreshHomePage() {

        new RefreshWorker(new RefreshWorker.OnRefreshListener() {
            @Override
            public void onRefresh(String html) {

                if (!TextUtils.isEmpty(html)) {

                    articles.clear();
                    articles.addAll(ArctileBlockConverter.converter(HomePagerParser.parserHomePage(html)));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mHomePageAdapter.notifyDataSetChanged();
                        mRefreshLayout.setRefreshing(false);
                    }
                });

            }
        }, mSecondCache, URLManager.HOMEPAGE);
    }

    /**
     * 获取文章首页
     *
     * @param isRefresh 是否属于刷新
     */
    private void getHtmlAndParser(boolean isRefresh) {

        mRefreshLayout.setRefreshing(true);
        getArticles(isRefresh).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleBlock>>() {
                    @Override
                    public void onCompleted() {

                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRefreshLayout.setRefreshing(false);
                        Toast.makeText(mContext, "加载首页发生错误，请刷新重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<ArticleBlock> articleBlocks) {

                        mHomePageAdapter = new HomePageAdapter(getActivity(), articleBlocks);
                        mArticleBlokcListView.setAdapter(mHomePageAdapter);

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

                if (!isRefresh)
                    html = mSecondCache.getResponseFromDiskCache(URLManager.HOMEPAGE);

                if (TextUtils.isEmpty(html)) {
                    html = mSecondCache.getResponseFromNetwork(URLManager.HOMEPAGE);
                }

                if (TextUtils.isEmpty(html))
                    subscriber.onError(new NullPointerException("未解析到文章主体"));
                else
                    subscriber.onNext(ArctileBlockConverter.converter(HomePagerParser.parserHomePage(html)));

                subscriber.onCompleted();
            }
        });

    }



}
