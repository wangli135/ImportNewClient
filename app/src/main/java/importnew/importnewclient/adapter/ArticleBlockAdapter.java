package importnew.importnewclient.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.ui.ArticleContentActivity;
import importnew.importnewclient.ui.BaseFragment;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.utils.ThridCache;
import importnew.importnewclient.view.VerticalArticleView;

/**
 * Created by Xingfeng on 2016/4/30.
 */
public class ArticleBlockAdapter extends RecyclerView.Adapter<ArticleBlockAdapter.ArticleBlockVH> {

    /**
     * 记录所有正在下载或等待下载的任务
     */
    private Set<BitmapWorkerTask> taskCollection;


    private ThridCache mThridCache;

    /**
     * RecycleView实例
     */
    private RecyclerView mRecycleView;

    private List<ArticleBlock> datas;

    private Activity activity;
    /**
     * 选择的文章
     */
    private Article selectedArticle;

    public ArticleBlockAdapter(Activity activity, RecyclerView recyclerView, List<ArticleBlock> datas) {
        this.datas = datas;
        this.mRecycleView = recyclerView;
        taskCollection = new HashSet<>();

        this.activity = activity;
        mThridCache = ThridCache.getInstance(activity);

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
            verticalArticleView.setImageResource(R.drawable.emptyview);
            loadBitmaps(verticalArticleView, article.getImgUrl());

            verticalArticleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedArticle = ((VerticalArticleView) v).getArticle();
                    if (activity instanceof BaseFragment.OnArticleSelectedListener) {
                        ((BaseFragment.OnArticleSelectedListener) activity).onArticleSelectedListener(selectedArticle);
                    }

                    Intent intent = new Intent(activity, ArticleContentActivity.class);
                    intent.putExtra(Constants.Key.ARTICLE, selectedArticle);
                    activity.startActivityForResult(intent, Constants.Code.REQUEST_CODE);

                }
            });
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
        if (mThridCache != null) {
            mThridCache.flushCache();
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
        }
    }

    public void loadBitmaps(VerticalArticleView articleView, String imageUrl) {

        try {

            Bitmap bitmap = mThridCache.getBitmapFromMemory(imageUrl);
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

            Bitmap bitmap = mThridCache.getBitmapFromDiskCache(imageUrl);
            if (bitmap == null) {
                bitmap = mThridCache.getBitmapFromNetwork(imageUrl);
            }
            return bitmap;
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
    }
}
