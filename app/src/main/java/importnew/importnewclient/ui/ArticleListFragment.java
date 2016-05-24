package importnew.importnewclient.ui;


import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.concurrent.TimeUnit;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.net.ConnectionManager;
import importnew.importnewclient.net.RefreshWorker;
import importnew.importnewclient.parser.ArticlesParser;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.utils.SecondCache;
import importnew.importnewclient.view.LoadMoreListView;

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
                mSwipeRefreshLayout.setRefreshing(true);
                if (ConnectionManager.isOnline(mContext)) {
                    refreshArticles();
                } else {

                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (ConnectionManager.isOnline(mContext)) {
                        refreshArticles();
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(mContext, R.string.network_unable, Toast.LENGTH_SHORT).show();
                    }

                }

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
        loadArticles();


    }


    private void refreshArticles() {


        new RefreshWorker(new RefreshWorker.OnRefreshListener() {
            @Override
            public void onRefresh(String html) {

                if (!TextUtils.isEmpty(html)) {
                    List<Article> list = ArticlesParser.parserArtciles(html);

                    for (int i = 0; i < list.size(); i++) {


                        if (!mArticles.contains(list.get(i))) {
                            mArticles.add(i, list.get(i));
                        } else
                            break;
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        }, mSecondCache, category + "1");
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
        new ArticleGetTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
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

    private long lastTime;

    class ArticleGetTask extends AsyncTask<String, Void, List<Article>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isLoading)
                mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<Article> doInBackground(String... params) {

            String url = params[0];
            String html = mSecondCache.getResponseFromDiskCache(url);
            if (TextUtils.isEmpty(html))
                html = mSecondCache.getResponseFromNetwork(url);

            if (!TextUtils.isEmpty(html)) {
                return ArticlesParser.parserArtciles(html);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Article> articles) {
            super.onPostExecute(articles);

            if (articles == null || articles.size() == 0) {
                mListView.setNoContentToLoad();
                mAdapter.notifyDataSetChanged();

                if (System.currentTimeMillis() - lastTime > 2000) {
                    Toast.makeText(mContext, "没有更多文章了", Toast.LENGTH_SHORT).show();
                    lastTime = System.currentTimeMillis();
                }

            } else if (articles.size() > 0) {

                if (mArticles.size() > 0) {

                    /**
                     * 无重合
                     */
                    int index = -1;
                    if ((index = articles.indexOf(mArticles.get(mArticles.size() - 1))) == -1) {
                        mArticles.addAll(articles);
                    } else {
                        for (int i = index + 1; i < articles.size(); i++) {
                            mArticles.add(articles.get(i));
                        }
                    }

                } else {
                    mArticles.addAll(articles);
                }

                mAdapter.notifyDataSetChanged();

            }

            isLoading = false;
            mSwipeRefreshLayout.setRefreshing(false);
            mListView.dismissFootView();
        }
    }


}
