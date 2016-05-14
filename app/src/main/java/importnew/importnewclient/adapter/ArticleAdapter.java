package importnew.importnewclient.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.utils.ThridCache;


/**
 * Created by Xingfeng on 2016/5/3.
 */
public class ArticleAdapter extends BaseAdapter {

    private List<Article> articles;
    private LayoutInflater mInflater;

    private Bitmap mLoadingBitmap;
    private ListView listView;
    //三级缓存
    private ThridCache mThridCache;

    private Set<BitmapWorkerTask> tasks;

    private Context mContext;

    public ArticleAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mThridCache = ThridCache.getInstance(context);
        tasks = new HashSet<>();
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

        if (listView == null)
            listView = (ListView) parent;

        Article article = articles.get(position);

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.article_hoizontall_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.article_title);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.article_img);
            viewHolder.img.setTag(article.getImgUrl());
            viewHolder.desc = (TextView) convertView.findViewById(R.id.article_desc);
            viewHolder.commentNum = (TextView) convertView.findViewById(R.id.article_comment_num);
            viewHolder.date = (TextView) convertView.findViewById(R.id.article_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(article.getTitle());
        viewHolder.desc.setText(article.getDesc());
        viewHolder.img.setImageResource(R.drawable.emptyview);
        viewHolder.commentNum.setText(article.getCommentNum());
        viewHolder.date.setText(article.getDate());

        loadBitmaps(article.getImgUrl(), viewHolder.img);
        return convertView;
    }

    private void loadBitmaps(String url, final ImageView imageView) {

        if (url == null) {
            return;
        }

        //Step 1：从内存中检索
        Bitmap bitmap = mThridCache.getBitmapFromMemory(url);
        if (bitmap != null && imageView != null) {
            imageView.setImageBitmap(bitmap);
        } else if (cancelPotentialWork(url, imageView)) {
            //Step 2:从硬盘中获取
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            AsyncDrawable asyncDrawable = new AsyncDrawable(mContext
                    .getResources(), mLoadingBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(url);
        }
    }

    public void cancelAllTasks() {
        for (BitmapWorkerTask task : tasks) {
            task.cancel(true);
        }
    }

    public void flushCache() {
        if (mThridCache != null) {
            mThridCache.flushCache();
        }
    }

    /**
     * 获取传入的ImageView它所对应的BitmapWorkerTask。
     */
    private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * 取消掉后台的潜在任务，当认为当前ImageView存在着一个另外图片请求任务时
     * ，则把它取消掉并返回true，否则返回false。
     */
    public boolean cancelPotentialWork(String url, ImageView imageView) {
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            String imageUrl = bitmapWorkerTask.imageUrl;
            if (imageUrl == null || !imageUrl.equals(url)) {
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    class ViewHolder {
        TextView title;
        ImageView img;
        TextView desc;
        TextView commentNum;
        TextView date;
    }


    /**
     * 自定义的一个Drawable，让这个Drawable持有BitmapWorkerTask的弱引用。
     */
    class AsyncDrawable extends BitmapDrawable {

        private WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
                    bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }

    }


    /**
     * 获取Bitmap，先从硬盘缓存中，再从网络
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, BitmapDrawable> {

        private String imageUrl;

        private WeakReference<ImageView> imageViewWeakReference;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected BitmapDrawable doInBackground(String... params) {

            imageUrl = params[0];

            Bitmap bitmap = mThridCache.getBitmapFromDiskCache(imageUrl);
            if (bitmap == null) {
                bitmap = mThridCache.getBitmapFromNetwork(imageUrl);
            }

            if (bitmap != null)
                return new BitmapDrawable(mContext.getResources(), bitmap);

            return null;

        }


        @Override
        protected void onPostExecute(BitmapDrawable bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = getAttachedImageView();
            if (bitmap != null && imageView != null) {
                imageView.setImageDrawable(bitmap);
            }
            tasks.remove(this);
        }

        /**
         * 获取当前BitmapWorkerTask所关联的ImageView。
         */
        private ImageView getAttachedImageView() {
            ImageView imageView = imageViewWeakReference.get();
            BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask) {
                return imageView;
            }
            return null;
        }
    }
}
