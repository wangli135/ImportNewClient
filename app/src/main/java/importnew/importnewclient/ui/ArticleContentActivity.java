package importnew.importnewclient.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.ListView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleBodyAdapter;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.bean.ArticleBody;
import importnew.importnewclient.parser.ArticleBodyParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 显示文章详情的页面
 */
public class ArticleContentActivity extends AppCompatActivity {

    public static final String ARTICLE_KEY="article_key";

    private Article mArticle;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private ArticleBodyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content);
        mArticle= (Article) getIntent().getSerializableExtra(ARTICLE_KEY);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

    }

    private void initViews(){

        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.article_swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mListView=(ListView)findViewById(R.id.article_content_lv);

        if(mArticle.getBody()!=null){
            mSwipeRefreshLayout.setEnabled(false);
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter=new ArticleBodyAdapter(this,mArticle.getBody());
            mListView.setAdapter(mAdapter);
        }else{
            new LoadAndParserWorker().execute(mArticle.getUrl());
        }
    }

    class LoadAndParserWorker extends AsyncTask<String,Void,ArticleBody>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected ArticleBody doInBackground(String... params) {

            try {
                String articleURL=params[0];
                OkHttpClient.Builder builder=new OkHttpClient.Builder();
                builder.connectTimeout(5, TimeUnit.SECONDS).readTimeout(10,TimeUnit.SECONDS)
                        .retryOnConnectionFailure(false);
                OkHttpClient client=builder.build();
                Request request=new Request.Builder().url(articleURL).build();
                Response response=client.newCall(request).execute();
                if(response.isSuccessful()){

                    ArticleBody articleBody= ArticleBodyParser.parser(response.body().string());
                    return articleBody;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(ArticleBody articleBody) {
            super.onPostExecute(articleBody);
            mSwipeRefreshLayout.setRefreshing(false);
            if(articleBody!=null){
                mArticle.setBody(articleBody);
                mAdapter=new ArticleBodyAdapter(ArticleContentActivity.this,articleBody);
                mListView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setEnabled(false);
            }else{
                mListView.setEmptyView(null);
            }
        }
    }
}
