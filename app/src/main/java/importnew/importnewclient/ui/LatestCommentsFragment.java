package importnew.importnewclient.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.HotArticleAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.net.URLManager;
import importnew.importnewclient.parser.HotArticlesParser;
import importnew.importnewclient.view.DividerItemDecoration;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 年度热门文章
 */
public class LatestCommentsFragment extends BaseFragment {


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecycleView;
    private List<Article> mArticles;
    private HotArticleAdapter mAdapter;

    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_most_discussed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.most_discussed_swiperefreshview);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHotArticles(true);
            }
        });

        mRecycleView = (RecyclerView) view.findViewById(R.id.most_discussed_recycleview);
        mRecycleView.setHasFixedSize(true);


        mArticles = new ArrayList<>();
        mAdapter = new HotArticleAdapter(getActivity(), mArticles);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecycleView.setAdapter(mAdapter);

        loadHotArticles(false);

    }

    /**
     * 是否刷新
     *
     * @param isRefresh
     */
    private void loadHotArticles(final boolean isRefresh) {

        mSwipeRefreshLayout.setRefreshing(true);
        mRecycleView.setVisibility(View.INVISIBLE);
        Observable<List<Article>> observable = Observable.create(new Observable.OnSubscribe<List<Article>>() {

            @Override
            public void call(Subscriber<? super List<Article>> subscriber) {
                String url = URLManager.HOMEPAGE;
                String html = "";
                if (!isRefresh)
                    html = mSecondCache.getResponseFromDiskCache(url);

                if (TextUtils.isEmpty(html))
                    html = mSecondCache.getResponseFromNetwork(url);

                if (!TextUtils.isEmpty(html)) {
                    List<Article> articles = HotArticlesParser.parserMostCommentsArticle(html);
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
                mSwipeRefreshLayout.setRefreshing(false);
                mRecycleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable e) {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "加载文章列表发生错误，请重新刷新", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(List<Article> articleList) {

                mAdapter = new HotArticleAdapter(getActivity(), articleList);
                mRecycleView.setAdapter(mAdapter);

            }
        });

    }


}
