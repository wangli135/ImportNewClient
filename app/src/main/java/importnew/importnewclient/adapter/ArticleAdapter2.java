package importnew.importnewclient.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.ui.ArticleContentActivity;
import importnew.importnewclient.ui.BaseFragment;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.utils.ImageLoader;

/**
 * Created by Xingfeng on 2016/5/23.
 */
public class ArticleAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LOADMORE = 1;//加载更多类型
    private static final int TYPE_NORMAL = 2;//正常类型

    private Activity mActivity;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private RecyclerView mRecyclerView;
    private VelocityTracker mVelocityTracker;
    private List<Article> mArticles;
    private OnLoadMoreListener onLoadMoreListener;
    private LinearLayoutManager layoutManager;
    private boolean canLoadBitmaps;//能否加载图片的标志
    private boolean isLoading;//正在加载更多的标志
    private boolean canLoading = true;//能否加载更多的标志

    private static final int VELOCITY = 500;

    private int firstVisibleItem;//第一个可见的Item的位置
    private int lastVisibleItem;//最后一个可见的Item的位置
    private int totalItemNum;

    private Set<BitmapWorkerTask> tasks;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public ArticleAdapter2(Activity activity, RecyclerView recyclerView, List<Article> articles) {
        mActivity = activity;
        mRecyclerView = recyclerView;
        mArticles = articles;

        mInflater = LayoutInflater.from(activity);
        mImageLoader = ImageLoader.getInstance(activity);
        mImageLoader.setRecycleView(mRecyclerView);
        layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        mVelocityTracker = VelocityTracker.obtain();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //不在滚动
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    loadBitmaps(firstVisibleItem, lastVisibleItem);
                }
                //在滚动
                else {

                    mVelocityTracker.computeCurrentVelocity(100);
                    if (Math.abs(mVelocityTracker.getYVelocity()) < VELOCITY) {
                        canLoadBitmaps = true;
                    } else {
                        canLoadBitmaps = false;
                    }

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                totalItemNum = layoutManager.getItemCount();
                if (canLoading && lastVisibleItem == totalItemNum - 1 && !isLoading && onLoadMoreListener != null) {
                    isLoading = true;
                    onLoadMoreListener.onLoadMore();
                }

            }
        });

        tasks = new HashSet<>();

    }

    /**
     * 设置加载完成
     */
    public void setLoadComplete() {
        isLoading = false;
    }

    /**
     * 设置不能加载更多
     */
    public void setCannotLoad() {
        canLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        View view = null;

        if (viewType == TYPE_LOADMORE) {
            view = mInflater.inflate(R.layout.listview_foot_view, parent, false);
            viewHolder = new LaodMoreVH(view);
        } else {
            view = mInflater.inflate(R.layout.article_hoizontall_layout, parent, false);
            viewHolder = new ArticleVH(view);
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_LOADMORE)
            return;

        ArticleVH articleVH = null;
        if (holder instanceof ArticleVH)
            articleVH = (ArticleVH) holder;

        Article article = mArticles.get(position);
        articleVH.setArticle(article);


        articleVH.title.setText(article.getTitle());
        articleVH.desc.setText(article.getDesc());
        articleVH.img.setImageResource(R.drawable.emptyview);
        articleVH.img.setTag(article.getImgUrl());
        articleVH.comment.setText(article.getCommentNum());
        articleVH.date.setText(article.getDate());

        if (canLoadBitmaps)
            loadBitmaps(article.getImgUrl(), articleVH.img);

    }

    private void loadBitmaps(String imageUrl, ImageView imageView) {

        Bitmap bitmap = mImageLoader.getBitmapFromMemory(imageUrl);
        if (bitmap != null && bitmap.getWidth() <= imageView.getWidth() && bitmap.getHeight() <= imageView.getHeight()) {
            imageView.setImageBitmap(bitmap);
        } else {
            BitmapWorkerTask task = new BitmapWorkerTask();
            tasks.add(task);
            task.execute(imageUrl);
        }

    }

    private void loadBitmaps(int first, int end) {

        int myFirst = first;
        int myEnd = end;
        if (getItemViewType(myEnd) == TYPE_LOADMORE)
            myEnd--;

        for (int i = myFirst; i <= myEnd; i++) {
            BitmapWorkerTask task = new BitmapWorkerTask();
            tasks.add(task);
            task.execute(mArticles.get(i).getImgUrl());
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (mArticles.get(position) == null)
            return TYPE_LOADMORE;
        else
            return TYPE_NORMAL;

    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    class LaodMoreVH extends RecyclerView.ViewHolder {

        public LaodMoreVH(View itemView) {
            super(itemView);
        }
    }

    class ArticleVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;//标题
        ImageView img;//图片
        TextView desc;//简述
        TextView date;//日期
        TextView comment;//评论数目

        private Article article;

        public void setArticle(Article article) {
            this.article = article;
        }

        public ArticleVH(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.article_title);
            img = (ImageView) itemView.findViewById(R.id.article_img);
            desc = (TextView) itemView.findViewById(R.id.article_desc);
            date = (TextView) itemView.findViewById(R.id.article_date);
            comment = (TextView) itemView.findViewById(R.id.article_comment_num);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (article == null)
                return;


            Intent intent = new Intent(mActivity, ArticleContentActivity.class);
            intent.putExtra(Constants.Key.ARTICLE, article);

            if (mActivity instanceof BaseFragment.OnArticleSelectedListener)
                ((BaseFragment.OnArticleSelectedListener) mActivity).onArticleSelectedListener(article);

            mActivity.startActivityForResult(intent, Constants.Code.REQUEST_CODE);

        }
    }

    /**
     * 获取Bitmap，先从硬盘缓存中，再从网络
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

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
            ImageView imageView = (ImageView) mRecyclerView.findViewWithTag(imageUrl);
            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
            tasks.remove(this);
        }

    }
}
