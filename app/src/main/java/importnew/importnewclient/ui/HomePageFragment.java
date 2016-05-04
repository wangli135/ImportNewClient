package importnew.importnewclient.ui;


import android.content.Context;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleBlockAdapter;
import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.net.URLManager;
import importnew.importnewclient.parser.HomePagerParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 首页Fragment
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private Context mContext;

    private List<ArticleBlock> articles;
    private ArticleBlockAdapter mAdapter;

    private ArticleBlockWorker articleBlockWorker;

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {


            super.onScrollStateChanged(recyclerView, newState);


        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

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
                getHtmlAndParser();
            }
        });


        mRecyclerView = (RecyclerView) view.findViewById(R.id.homepage_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        articles = new ArrayList<>();
        mAdapter = new ArticleBlockAdapter(getActivity(), mRecyclerView, articles);
        mRecyclerView.setAdapter(mAdapter);
        getHtmlAndParser();

        mRecyclerView.addOnScrollListener(onScrollListener);

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

        mRecyclerView.removeOnScrollListener(onScrollListener);
    }

    private void getHtmlAndParser() {

        articleBlockWorker = new ArticleBlockWorker();
        articleBlockWorker.execute(URLManager.HOMEPAGE);
    }


    class ArticleBlockWorker extends AsyncTask<String, Void, List<ArticleBlock>> {

        @Override
        protected List<ArticleBlock> doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS).build();
                Request request = new Request.Builder().url(params[0]).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {

                    List<ArticleBlock> articleBlockList = HomePagerParser.paserHomePage(response.body().string());
                    return articleBlockList;

                } else {
                    return null;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
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
