package importnew.importnewclient.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleBlockAdapter;
import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.net.HttpManager;
import importnew.importnewclient.net.RefreshWorker;
import importnew.importnewclient.net.URLManager;
import importnew.importnewclient.parser.HomePagerParser;


/**
 * 首页Fragment
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    private List<ArticleBlock> articles;
    private ArticleBlockAdapter mAdapter;

    private ArticleBlockWorker articleBlockWorker;


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

                mRefreshLayout.setRefreshing(true);
                refreshHomePage();
            }
        });


        mRecyclerView = (RecyclerView) view.findViewById(R.id.homepage_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        articles = new ArrayList<>();
        mAdapter = new ArticleBlockAdapter(getActivity(), mRecyclerView, articles);
        mRecyclerView.setAdapter(mAdapter);
        getHtmlAndParser();


    }


    @Override
    public void onPause() {
        super.onPause();

        mRefreshLayout.setRefreshing(false);

        if (mAdapter != null)
            mAdapter.flushCache();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRefreshLayout.setRefreshing(false);

        if (mAdapter != null)
            mAdapter.cancelAllTasks();

        if (articleBlockWorker != null) {
            articleBlockWorker.cancel(true);
        }

    }

    /**
     * 刷新页面
     */
    private void refreshHomePage() {

        if (httpClient == null)
            httpClient = HttpManager.getInstance(mContext).getClient();
        new RefreshWorker(new RefreshWorker.OnRefreshListener() {
            @Override
            public void onRefresh(String html) {

                if (html != null) {
                    List<ArticleBlock> blocks = HomePagerParser.paserHomePage(html);
                    articles.clear();
                    articles.addAll(blocks);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mRefreshLayout.setRefreshing(false);
                    }
                });

            }
        }, httpClient, URLManager.HOMEPAGE);
    }

    private void getHtmlAndParser() {

        articleBlockWorker = new ArticleBlockWorker();
        articleBlockWorker.execute(URLManager.HOMEPAGE);
    }


    class ArticleBlockWorker extends AsyncTask<String, Void, List<ArticleBlock>> {

        @Override
        protected List<ArticleBlock> doInBackground(String... params) {

            String url = params[0];
            String html = mSecondCache.getResponseFromDiskCache(url);
            if (html == null)
                html = mSecondCache.getResponseFromNetwork(url);

            if (html != null)
                return HomePagerParser.paserHomePage(html);
            else
                return null;


        }

        @Override
        protected void onPostExecute(List<ArticleBlock> articleBlocks) {
            super.onPostExecute(articleBlocks);

            mRefreshLayout.setRefreshing(false);

            if (articleBlocks != null) {

                articles.clear();
                articles.addAll(articleBlocks);
                mAdapter.notifyDataSetChanged();
            }

        }
    }


}
