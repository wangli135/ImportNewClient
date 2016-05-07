package importnew.importnewclient.ui;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.net.HttpManager;
import importnew.importnewclient.net.RefreshWorker;
import importnew.importnewclient.parser.ArticlesParser;
import importnew.importnewclient.utils.SecondCache;
import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends Fragment implements ListView.OnItemClickListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;

    private LinkedList<Article> mArticles;
    private ArticleAdapter mAdapter;

    private SecondCache mSecondCache;
    private OkHttpClient httpClient;

    /**
     * 文章分类URL
     */
    private String category;

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
        if (savedInstanceState != null) {
            category = (String) savedInstanceState.get(ARTICLE_BASE_URL);
        }
    }

    public static ArticleListFragment newInstance(String baseurl) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle bundle = new Bundle();
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
                refreshArticles();

            }
        });

        mListView = (ListView) view.findViewById(R.id.articles_lv);
        mArticles =new LinkedList<>();
        mAdapter = new ArticleAdapter(getParentFragment().getActivity(), mArticles);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mSecondCache=SecondCache.getInstance(getContext());

        loadArticles();
    }


    private void refreshArticles(){
        if(httpClient==null)
            httpClient= HttpManager.getInstance(getContext()).getClient();
        new RefreshWorker(new RefreshWorker.OnRefreshListener() {
            @Override
            public void onRefresh(String html) {

                if(html==null){
                    mSwipeRefreshLayout.setRefreshing(false);
                }else{
                    List<Article> list=ArticlesParser.parserArtciles(html);
                    for(Article article:list){
                        if(!mArticles.contains(article)){
                            mArticles.addFirst(article);
                        }else
                            break;
                    }
                    mAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        },httpClient,category+"1");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.flushCache();

        if(mSecondCache!=null)
            mSecondCache.flushCache();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.cancelAllTasks();
    }

    private void loadArticles() {
        category=(String)getArguments().get(ARTICLE_BASE_URL);
        String url =category+ pageNum;
        new ArticleGetTask().execute(url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Article article = mArticles.get(position);
        Intent intent = new Intent(getParentFragment().getActivity(), ArticleContentActivity.class);
        intent.putExtra(ArticleContentActivity.ARTICLE_KEY, article);
        getParentFragment().getActivity().startActivity(intent);

    }

    class ArticleGetTask extends AsyncTask<String, Void, List<Article>> {

        @Override
        protected List<Article> doInBackground(String... params) {

            String url=params[0];
            String html=mSecondCache.getResponseFromDiskCache(url);
            if(html==null)
                html=mSecondCache.getResponseFromNetwork(url);

            if(html!=null){
                return ArticlesParser.parserArtciles(html);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Article> articles) {
            super.onPostExecute(articles);

            if (articles == null) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                mArticles.addAll(articles);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }


}
