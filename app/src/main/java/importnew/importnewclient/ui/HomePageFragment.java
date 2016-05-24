package importnew.importnewclient.ui;


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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.HomePageAdapter;
import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.net.ConnectionManager;
import importnew.importnewclient.net.RefreshWorker;
import importnew.importnewclient.net.URLManager;
import importnew.importnewclient.parser.HomePagerParser;
import importnew.importnewclient.utils.ArctileBlockConverter;


/**
 * 首页Fragment
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends BaseFragment {


    private SwipeRefreshLayout mRefreshLayout;

    private List<ArticleBlock> articles;

    private ArticleBlockWorker articleBlockWorker;
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

                mRefreshLayout.setRefreshing(true);
                if (ConnectionManager.isOnline(mContext)) {
                    refreshHomePage();
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (ConnectionManager.isOnline(mContext)) {
                        refreshHomePage();
                    } else {
                        mRefreshLayout.setRefreshing(false);
                        Toast.makeText(mContext, R.string.network_unable, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        mArticleBlokcListView = (ListView) view.findViewById(R.id.article_block_listview);
        articles = new ArrayList<>();
        mHomePageAdapter = new HomePageAdapter(getActivity(), articles);
        mArticleBlokcListView.setAdapter(mHomePageAdapter);

        getHtmlAndParser();


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

        if (articleBlockWorker != null) {
            articleBlockWorker.cancel(true);
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

    private void getHtmlAndParser() {

        articleBlockWorker = new ArticleBlockWorker();
        articleBlockWorker.execute(URLManager.HOMEPAGE);
    }


    class ArticleBlockWorker extends AsyncTask<String, Void, List<ArticleBlock>> {

        @Override
        protected List<ArticleBlock> doInBackground(String... params) {

            String url = params[0];
            String html = mSecondCache.getResponseFromDiskCache(url);
            if (TextUtils.isEmpty(html))
                html = mSecondCache.getResponseFromNetwork(url);

            if (!TextUtils.isEmpty(html)) {
                return ArctileBlockConverter.converter(HomePagerParser.parserHomePage(html));
            } else
                return null;

        }

        @Override
        protected void onPostExecute(List<ArticleBlock> articleBlocks) {
            super.onPostExecute(articleBlocks);

            mRefreshLayout.setRefreshing(false);

            if (articleBlocks != null) {

                articles.clear();
                articles.addAll(articleBlocks);
                mHomePageAdapter.notifyDataSetChanged();
            }
        }
    }


}
