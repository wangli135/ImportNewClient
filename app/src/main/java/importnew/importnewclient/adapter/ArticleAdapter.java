package importnew.importnewclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.loader.ImageAware;
import importnew.importnewclient.loader.ImageLoader;
import importnew.importnewclient.utils.DisplayUtil;


/**
 * Created by Xingfeng on 2016/5/3.
 */
public class ArticleAdapter extends BaseAdapter implements View.OnTouchListener, AbsListView.OnScrollListener {

    private List<Article> articles;
    private LayoutInflater mInflater;

    private ImageLoader mImageLoader;

    private Context mContext;

    private ListView mListView;

    private VelocityTracker mVelocityTracker;

    private static final int VELOCITY = 500;

    private boolean canLoadBitmaps = true;

    private int mStart;
    private int mEnd;

    public ArticleAdapter(Context context, List<Article> articles, ListView listView) {
        this.articles = articles;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mImageLoader = ImageLoader.getInstance(context);

        mVelocityTracker = VelocityTracker.obtain();

        mListView = listView;
        listView.setOnTouchListener(this);
        listView.setOnScrollListener(this);

    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Article article = articles.get(position);

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.article_hoizontall_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.article_title);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.article_img);
            viewHolder.desc = (TextView) convertView.findViewById(R.id.article_desc);
            viewHolder.commentNum = (TextView) convertView.findViewById(R.id.article_comment_num);
            viewHolder.date = (TextView) convertView.findViewById(R.id.article_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(article.getTitle());
        viewHolder.desc.setText(article.getDesc());
        viewHolder.img.setVisibility(View.VISIBLE);
        viewHolder.img.setImageResource(R.drawable.emptyview);
        viewHolder.commentNum.setText(article.getCommentNum());
        viewHolder.date.setText(article.getDate());

        if (canLoadBitmaps)
            loadBitmaps(article.getImgUrl(), viewHolder.img);

        return convertView;
    }

    private void loadBitmaps(final String url, final ImageView imageView) {

        if (url == null) {
            imageView.setVisibility(View.GONE);
        }

        int width = DisplayUtil.dp2sp(mContext, 48);
        int height = width;
        ImageAware imageAware = new ImageAware(url, imageView, width, height);
        mImageLoader.loadBitmap(url, imageAware);

    }

    public void cancelAllTasks() {


    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mVelocityTracker.addMovement(event);
        return false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == SCROLL_STATE_IDLE) {

            canLoadBitmaps = true;
            notifyDataSetChanged();

        } else {
            mVelocityTracker.computeCurrentVelocity(100);
            if (Math.abs(mVelocityTracker.getYVelocity()) > VELOCITY) {
                canLoadBitmaps = false;
            } else {
                canLoadBitmaps = true;
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
    }


    class ViewHolder {
        TextView title;
        ImageView img;
        TextView desc;
        TextView commentNum;
        TextView date;
    }

}
