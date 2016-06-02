package importnew.importnewclient.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.parser.ArticlesParser;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.utils.SecondCache;
import importnew.importnewclient.view.LoadMoreListView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends BaseFragment implements ListView.OnItemClickListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LoadMoreListView mListView;

    private ArrayList<Article> mArticles;
    private ArticleAdapter mAdapter;

    private boolean isLoading;//加载更多的标志

    /**
     * 文章分类URL
     */
    private String category;

    /**
     * 文章页数
     */
    private int pageNum = 1;

    public ArticleListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            category = (String) savedInstanceState.get(Constants.Key.ARTICLE_BASE_URL);
            mArticles = savedInstanceState.getParcelableArrayList(Constants.Key.ARTICLE_LIST);
            pageNum = savedInstanceState.getInt(Constants.Key.PAGE_NUM);
        } else pageNum = 1;
    }

    public static ArticleListFragment newInstance(String baseurl) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Key.ARTICLE_BASE_URL, baseurl);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.article_swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setEnabled(true);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshArticles();
            }
        });

        mListView = (LoadMoreListView) view.findViewById(R.id.articles_lv);
        mListView.setOnItemClickListener(this);
        mListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoad() {

                isLoading = true;
                loadArticles();

            }
        });

        mSecondCache = SecondCache.getInstance(getContext());
        mArticles = new ArrayList<>();
        mAdapter = new ArticleAdapter(getParentFragment().getActivity(), mArticles, mListView);
        mListView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setRefreshing(true);
        loadArticles();


    }


    private void refreshArticles() {

        mSwipeRefreshLayout.setRefreshing(true);
        Observable<List<Article>> observable = Observable.create(new Observable.OnSubscribe<List<Article>>() {
            @Override
            public void call(Subscriber<? super List<Article>> subscriber) {

                String html = mSecondCache.getResponseFromNetwork(category + 1);

                if (TextUtils.isEmpty(html))
                    subscriber.onError(new Exception("刷新出错"));
                else {
                    List<Article> articles = ArticlesParser.parserArtciles(html);
                    subscriber.onNext(articles);
                    subscriber.onCompleted();
                }

            }
        });

        observable.onBackpressureBuffer().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Article>>() {
            @Override
            public void onCompleted() {

                mSwipeRefreshLayout.setRefreshing(false);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(Throwable e) {

                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "加载页面出错，请重新刷新", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.cancelAllTasks();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.Key.ARTICLE_BASE_URL, category);
        outState.putParcelableArrayList(Constants.Key.ARTICLE_LIST, mArticles);
        outState.putInt(Constants.Key.PAGE_NUM, pageNum);
        super.onSaveInstanceState(outState);

    }

    private void loadArticles() {
        category = (String) getArguments().get(Constants.Key.ARTICLE_BASE_URL);
        String url = category + (pageNum++);


        parserArticles(url).onBackpressureBuffer().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
                            Toast.makeText(getActivity(), "加载内容发生错误，请重试", Toast.LENGTH_SHORT).show();
                        else {
                            mListView.setNoContentToLoad();
                        }

                    }

                    @Override
                    public void onNext(List<Article> articleList) {

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

    private Observable<List<Article>> parserArticles(final String url) {

        return Observable.create(new Observable.OnSubscribe<List<Article>>() {
            @Override
            public void call(Subscriber<? super List<Article>> subscriber) {

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Article article = mArticles.get(position);
        Intent intent = new Intent(getParentFragment().getActivity(), ArticleContentActivity.class);
        intent.putExtra(Constants.Key.ARTICLE, article);

        if (getActivity() instanceof BaseFragment.OnArticleSelectedListener)
            ((OnArticleSelectedListener) getActivity()).onArticleSelectedListener(article);

        getActivity().startActivityForResult(intent, Constants.Code.REQUEST_CODE);

    }


}
