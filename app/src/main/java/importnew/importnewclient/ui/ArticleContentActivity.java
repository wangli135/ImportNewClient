package importnew.importnewclient.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.utils.SecondCache;
import importnew.importnewclient.view.ProgressWebView;
import okhttp3.Response;

/**
 * 显示文章详情的页面
 */
public class ArticleContentActivity extends AppCompatActivity {

    /**
     * 加载的URL
     */
    private String mLoadUrl;

    private Article mArticle;

    private ProgressWebView mWebView;

    private SecondCache mSecondCache;

    private boolean canShare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content);
        mArticle = (Article) getIntent().getParcelableExtra(Constants.Key.ARTICLE);
        mLoadUrl = mArticle.getUrl();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mArticle.getTitle());
        mSecondCache = SecondCache.getInstance(this);

        initViews();

    }


    private View mErrorStub;

    private void initViews() {

        mWebView = (ProgressWebView) findViewById(R.id.article_webview);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setMyWebChromeClient(new ProgressWebView.MyWebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                getSupportActionBar().setTitle(title);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(final WebView view, int errorCode, String description, final String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (mErrorStub == null) {
                    mErrorStub = ((ViewStub) findViewById(R.id.stub_error)).inflate();
                    mErrorStub.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            view.loadUrl(mLoadUrl);
                        }
                    });
                }
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                String url = request.getUrl().toString();
                if (isArticleUrl(url)) {

                    Response httpResponse = mSecondCache.getResponse(url);
                    if(httpResponse!=null){
                        WebResourceResponse webResourceResponse = new WebResourceResponse("text/html", "utf-8", httpResponse.body().byteStream());
                        httpResponse.body().close();
                        return webResourceResponse;
                    }

                }

                return super.shouldInterceptRequest(view, request);
            }


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (isArticleUrl(url)) {

                    Response httpResponse = mSecondCache.getResponse(url);
                    if(httpResponse!=null){
                        WebResourceResponse webResourceResponse = new WebResourceResponse("text/html", "utf-8", httpResponse.body().byteStream());
                        httpResponse.body().close();
                        return webResourceResponse;
                    }

                }

                return super.shouldInterceptRequest(view, url);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isPicture(url)) {
                    Intent intent = new Intent(ArticleContentActivity.this, ShowPictureActivity.class);
                    intent.putExtra(Constants.Key.PICTURE_URL, url);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });

        mWebView.loadUrl(mLoadUrl);

    }

    /**
     * 是文章的请求
     *
     * @param url
     * @return
     */
    private boolean isArticleUrl(String url) {

        Pattern pattern = Pattern.compile(Constants.Regex.IS_ARTICLE_URL);
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    /**
     * 是图片的请求
     *
     * @param url
     * @return
     */
    private boolean isPicture(String url) {

        Pattern pattern = Pattern.compile(Constants.Regex.IS_PICTURE_URL);
        Matcher matcher = pattern.matcher(url);
        return matcher.find();


    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        mWebView.stopLoading();
        super.onDestroy();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_share: {

                if (!canShare) {
                    return true;
                }

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TITLE, mArticle.getTitle());
                share.putExtra(Intent.EXTRA_TEXT, mArticle.getDesc());
                share.putExtra(Intent.EXTRA_HTML_TEXT, mArticle.getUrl());
                startActivity(Intent.createChooser(share, "Share"));
            }
            break;

            case android.R.id.home:

                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                    break;
                }

                Intent intent = new Intent();
                intent.putExtra(Constants.Key.ARTICLE, mArticle);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
