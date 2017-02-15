package importnew.importnewclient.customview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import importnew.importnewclient.R;
import importnew.importnewclient.utils.DisplayUtil;

/**
 * Created by Xingfeng on 2016/6/6.
 */
public class ProgressWebView extends WebView {

    private ProgressBar mProgressBar;
    private MyWebChromeClient myWebChromeClient;

    public void setMyWebChromeClient(MyWebChromeClient myWebChromeClient) {
        this.myWebChromeClient = myWebChromeClient;
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mProgressBar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dp2sp(context, 4));
        mProgressBar.setLayoutParams(layoutParams);

        Drawable drawable = context.getResources().getDrawable(
                R.drawable.web_progress_bar_states);
        mProgressBar.setProgressDrawable(drawable);
        addView(mProgressBar);
        setWebChromeClient(new WebChromeClient());
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(GONE);
            } else {
                if (mProgressBar.getVisibility() == GONE)
                    mProgressBar.setVisibility(VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (myWebChromeClient != null)
                myWebChromeClient.onReceivedTitle(view, title);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) mProgressBar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        mProgressBar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public interface MyWebChromeClient {

        /**
         * Notify the host application of a change in the document title.
         *
         * @param view  The WebView that initiated the callback.
         * @param title A String containing the new title of the document.
         */
        void onReceivedTitle(WebView view, String title);


    }
}
