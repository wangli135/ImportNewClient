package importnew.importnewclient.ui;


import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.HotArticleAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.net.URLManager;
import importnew.importnewclient.parser.HotArticlesParser;

/**
 * 本月热门文章
 */
public class MostDiscussedFragment extends BaseFragment {

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

        mRecycleView = (RecyclerView) view.findViewById(R.id.most_discussed_recycleview);
        mRecycleView.setHasFixedSize(true);


        mArticles = new ArrayList<>();
        mAdapter = new HotArticleAdapter(getActivity(), mArticles);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setAdapter(mAdapter);

        loadHotArticles();

    }

    private void loadHotArticles() {

        new ArticleGetTask().execute();

    }

    class ArticleGetTask extends AsyncTask<Void, Void, List<Article>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<Article> doInBackground(Void... params) {

            String url = URLManager.HOMEPAGE;
            String html = mSecondCache.getResponseFromDiskCache(url);
            if (TextUtils.isEmpty(html))
                html = mSecondCache.getResponseFromNetwork(url);

            if (!TextUtils.isEmpty(html)) {
                return HotArticlesParser.parserHotDiscussedArticles(html);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Article> articles) {
            super.onPostExecute(articles);

            mSwipeRefreshLayout.setRefreshing(false);

            if (articles != null && articles.size() > 0) {
                mArticles.clear();
                mArticles.addAll(articles);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setEnabled(false);
            }

        }
    }
}
