package importnew.importnewclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.ui.ArticleContentActivity;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.utils.ImageLoader;

/**
 * Created by Xingfeng on 2016/5/16.
 */
public class ArticleBlockAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    private LayoutInflater mInfalter;

    private ArticleBlock mArticleBlock;
    private List<Article> articleList;

    private ListView mListView;

    /**
     * 记录所有正在下载或等待下载的任务
     */
    private Set<BitmapWorkerTask> taskCollection;

    private ImageLoader mImageLoader;

    private Context mContext;

    public ArticleBlockAdapter(Context context, ListView listView, ArticleBlock articleBlock) {
        mContext = context;
        mInfalter = LayoutInflater.from(context);
        mListView = listView;
        mArticleBlock = articleBlock;
        articleList = mArticleBlock.getArticles();

        taskCollection = new HashSet<>();
        mImageLoader = ImageLoader.getInstance(context.getApplicationContext());
        listView.setOnItemClickListener(this);
    }

    @Override
    public int getCount() {
        return articleList.size();
    }

    @Override
    public Object getItem(int position) {
        return articleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 5 == 0) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Article article = articleList.get(position);
        View view = null;

        if (getItemViewType(position) == 0) {
            view = mInfalter.inflate(R.layout.first_article_layout, parent, false);
            TextView category = (TextView) view.findViewById(R.id.articles_category);
            category.setText(mArticleBlock.getCategory());
        } else {
            view = mInfalter.inflate(R.layout.nonfirst_article_layout, parent, false);
        }

        TextView title = (TextView) view.findViewById(R.id.article_title);
        title.setText(article.getTitle());
        ImageView img = (ImageView) view.findViewById(R.id.article_img);
        loadBitmaps(img, article.getImgUrl());

        return view;
    }

    public void loadBitmaps(ImageView articleImgView, String imageUrl) {

        try {

            Bitmap bitmap = mImageLoader.getBitmapFromMemory(imageUrl);
            if (bitmap == null) {
                BitmapWorkerTask task = new BitmapWorkerTask(articleImgView);
                taskCollection.add(task);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageUrl);
            } else if (articleImgView != null && bitmap != null) {
                articleImgView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Article article = articleList.get(position);
        Intent intent = new Intent(mContext, ArticleContentActivity.class);
        intent.putExtra(Constants.Key.ARTICLE, article);

        Activity activity = (Activity) mContext;
        activity.startActivity(intent);

    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;

        public BitmapWorkerTask(ImageView mImageView) {
            this.mImageView = mImageView;
        }

        /**
         * 图片的URL地址
         */
        private String imageUrl;

        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];

            Bitmap bitmap = mImageLoader.getBitmap(imageUrl);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (mImageView != null && bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
            taskCollection.remove(this);

        }
    }

    /**
     * 将缓存记录同步到journal文件中
     */
    public void flushCache() {

    }

    /**
     * 取消所有正在下载或等待下载的任务
     */
    public void cancelAllTasks() {
        if (taskCollection != null) {
            for (BitmapWorkerTask task : taskCollection) {
                task.cancel(true);
            }
        }
    }


}
