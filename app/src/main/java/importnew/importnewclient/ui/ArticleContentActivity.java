package importnew.importnewclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.URI;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.utils.SecondCache;

/**
 * 显示文章详情的页面
 */
public class ArticleContentActivity extends AppCompatActivity {

    private Article mArticle;

    private ProgressBar mProgressBar;
    private WebView mWebView;

    private SecondCache mSecondCache;

    private LoadAndParserWorker worker;

    /**
     * 是否收藏
     */
    private boolean isFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content);
        mArticle = (Article) getIntent().getParcelableExtra(Constants.Key.ARTICLE);
        isFavourite = mArticle.isFavourite();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSecondCache = SecondCache.getInstance(this);

        initViews();

    }

    private void initViews() {

        mProgressBar = (ProgressBar) findViewById(R.id.article_progressbar);
        mWebView = (WebView) findViewById(R.id.article_webview);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (URI.create(url).getHost().equals("www.importnew.com") || (url.matches(".+\\.((jpg)|(png)|(gif))")))
                    return false;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

        worker = new LoadAndParserWorker();
        worker.execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (worker != null) {
            worker.cancel(true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_article_content_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_favourite);
        if (isFavourite)
            menuItem.setIcon(R.drawable.ic_menu_favorite_red);
        else
            menuItem.setIcon(R.drawable.ic_menu_favorite_white);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_favourite: {

                isFavourite = !isFavourite;
                if (isFavourite) {
                    item.setIcon(R.drawable.ic_menu_favorite_red);
                    Toast.makeText(this, R.string.favourite_success, Toast.LENGTH_SHORT).show();
                } else {
                    item.setIcon(R.drawable.ic_menu_favorite_white);
                    Toast.makeText(this, R.string.cancel_favourite, Toast.LENGTH_SHORT).show();
                }

            }
            break;
            case R.id.menu_share:
                break;

            case android.R.id.home:
                Intent intent = new Intent();
                mArticle.setFavourite(isFavourite);
                intent.putExtra(Constants.Key.ARTICLE, mArticle);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    class LoadAndParserWorker extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWebView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... params) {

            String html = mArticle.getBodyString();

            if (TextUtils.isEmpty(html)) {
                html = mSecondCache.getResponseFromDiskCache(mArticle.getUrl());
                if (TextUtils.isEmpty(html))
                    html = mSecondCache.getResponseFromNetwork(mArticle.getUrl());
            }

            if (!TextUtils.isEmpty(html)) {
                mArticle.setBodyString(html);
                return html;
            }

            return null;

        }

        @Override
        protected void onPostExecute(String html) {
            super.onPostExecute(html);
            if (!TextUtils.isEmpty(html)) {
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            }
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
