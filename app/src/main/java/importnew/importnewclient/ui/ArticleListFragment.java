package importnew.importnewclient.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.parser.ArticlesParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;

    private List<Article> mArticles;
    private ArticleAdapter mAdapter;

    /**
     * 文章分类URL
     */
    private  String url;

    /**
     * 文章页数
     */
    private int pageNum = 1;

    public static final String ARTICLE_BASE_URL = "article_base_url";

    public ArticleListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            url= (String) savedInstanceState.get(ARTICLE_BASE_URL);
        }
    }

    public static ArticleListFragment newInstance(String baseurl) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle bundle =new Bundle();
        bundle.putString(ARTICLE_BASE_URL, baseurl);
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
        mSwipeRefreshLayout.setRefreshing(true);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mListView.setVisibility(View.GONE);
                loadArticles();

            }
        });

        mListView = (ListView) view.findViewById(R.id.articles_lv);
        mArticles = new ArrayList<>();
        mAdapter = new ArticleAdapter(getParentFragment().getActivity(), mArticles);
        mListView.setAdapter(mAdapter);


        loadArticles();
    }


    @Override
    public void onPause() {
        super.onPause();
        mAdapter.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cancelAllTasks();
    }

    private void loadArticles(){
        String url=(String)getArguments().get(ARTICLE_BASE_URL)+pageNum;
        new ArticleGetTask().execute(url);
    }

    class ArticleGetTask extends AsyncTask<String,Void,List<Article>> {

        @Override
        protected List<Article> doInBackground(String... params) {

            try {
                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder().url(params[0]).build();
                Response response=client.newCall(request).execute();
                if(response.isSuccessful()){

                    List<Article> articles= ArticlesParser.parserArtciles(response.body().string());
                    return articles;

                }else{
                    return null;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Article> articles) {
            super.onPostExecute(articles);

            if(articles==null){
                mSwipeRefreshLayout.setRefreshing(false);
            }else{
                mArticles.addAll(articles);
                mSwipeRefreshLayout.setRefreshing(false);
                mListView.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }
        }
    }


}
