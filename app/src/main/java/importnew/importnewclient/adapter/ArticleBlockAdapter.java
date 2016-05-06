package importnew.importnewclient.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.utils.MyDiskLruCache;
import importnew.importnewclient.utils.MyLruCache;
import importnew.importnewclient.view.VerticalArticleView;

/**
 * Created by Xingfeng on 2016/4/30.
 */
public class ArticleBlockAdapter extends RecyclerView.Adapter<ArticleBlockAdapter.ArticleBlockVH> {

    /**
     * 记录所有正在下载或等待下载的任务
     */
    private Set<BitmapWorkerTask> taskCollection;


    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉
     */
    private MyLruCache mMemoryCache;

    /**
     * 图片硬盘缓存核心类
     */
    private MyDiskLruCache mDiskLruCache;

    /**
     * RecycleView实例
     */
    private RecyclerView mRecycleView;

    private List<ArticleBlock> datas;

    public ArticleBlockAdapter(Context context, RecyclerView recyclerView, List<ArticleBlock> datas) {
        this.datas = datas;
        this.mRecycleView = recyclerView;
        taskCollection = new HashSet<>();

        mMemoryCache = MyLruCache.newInstance();
        mDiskLruCache = new MyDiskLruCache(context, "thumb");
    }

    @Override
    public ArticleBlockVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.articles_block_layout, parent, false);
        return new ArticleBlockVH(view);
    }

    @Override
    public void onBindViewHolder(ArticleBlockVH holder, int position) {

        ArticleBlock articleBlock = datas.get(position);
        holder.category.setText(articleBlock.getCategory());
        List<Article> articles = articleBlock.getArticles();
        Article article = null;
        VerticalArticleView verticalArticleView = null;
        for (int i = 0; i < articles.size(); i++) {

            verticalArticleView = holder.views[i];
            article = articles.get(i);
            verticalArticleView.setArticle(article);
            verticalArticleView.setText(article.getTitle());
            verticalArticleView.setTag(article.getImgUrl());
            verticalArticleView.setImageResource(R.drawable.img_empty);
            loadBitmaps(verticalArticleView, article.getImgUrl());
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 将缓存记录同步到journal文件中
     */
    public void flushCache() {
        if (mDiskLruCache != null) {
            mDiskLruCache.flushCache();
        }
    }

    public static class ArticleBlockVH extends RecyclerView.ViewHolder {

        TextView category;
        VerticalArticleView[] views;


        public ArticleBlockVH(final View itemView) {
            super(itemView);
            category = (TextView) itemView.findViewById(R.id.articles_category);
            views = new VerticalArticleView[5];
            views[0] = (VerticalArticleView) itemView.findViewById(R.id.first_article);
            views[1] = (VerticalArticleView) itemView.findViewById(R.id.second_article);
            views[2] = (VerticalArticleView) itemView.findViewById(R.id.third_article);
            views[3] = (VerticalArticleView) itemView.findViewById(R.id.fourth_article);
            views[4] = (VerticalArticleView) itemView.findViewById(R.id.fifth_article);

            for (int i = 0; i < views.length; i++) {

                views[i].setStartActivity(true);

            }
        }


    }

    public void loadBitmaps(VerticalArticleView articleView, String imageUrl) {

        try {

            Bitmap bitmap = mMemoryCache.getBitmapFromCache(imageUrl);
            if (bitmap == null) {
                BitmapWorkerTask task = new BitmapWorkerTask();
                taskCollection.add(task);
                task.execute(imageUrl);
            } else if (articleView != null && bitmap != null) {
                articleView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


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

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        /**
         * 图片的URL地址
         */
        private String imageUrl;

        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            DiskLruCache.Snapshot snapshot = null;

            try {
                //查找key对应的缓存
                snapshot = mDiskLruCache.getCache(imageUrl);
                if (snapshot == null) {
                    //如果没有找到对应的缓存，则准备从网络上请求数据
                    downloadUrlToStream(imageUrl);
                    snapshot = mDiskLruCache.getCache(imageUrl);
                }

                if (snapshot != null) {
                    fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                }

                //将缓存数据解析成Bitmap
                Bitmap bitmap = null;
                if (fileDescriptor != null) {
                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                }

                if (bitmap != null) {
                    mMemoryCache.addBitmapToCache(params[0], bitmap);
                }
                return bitmap;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            VerticalArticleView view = (VerticalArticleView) mRecycleView.findViewWithTag(imageUrl);
            if (view != null && bitmap != null) {
                view.setImageBitmap(bitmap);
            }
            taskCollection.remove(this);

        }

        private boolean downloadUrlToStream(String urlString) {

            HttpURLConnection urlConnection = null;
            BufferedInputStream in = null;
            try {
                final URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                mDiskLruCache.putCache(urlString, in);
                return true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                try {

                    if (in != null) {
                        in.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return false;
        }


    }


}
