package importnew.importnewclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.ui.ArticleContentActivity;
import importnew.importnewclient.ui.BaseFragment;
import importnew.importnewclient.utils.Constants;

/**
 * Created by Xingfeng on 2016/5/21.
 */
public class HotArticleAdapter extends RecyclerView.Adapter<HotArticleAdapter.HotArticleVH> {

    private LayoutInflater mInfalter;
    private List<Article> mArticles;
    private Context mContext;

    public HotArticleAdapter(Context context, List<Article> articles) {
        mInfalter = LayoutInflater.from(context);
        mArticles = articles;
        mContext = context;
    }


    @Override
    public HotArticleVH onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInfalter.inflate(R.layout.hot_article_layout, parent, false);

        return new HotArticleVH(view);
    }

    @Override
    public void onBindViewHolder(HotArticleVH holder, int position) {
        holder.title.setText(mArticles.get(position).getTitle());
        holder.setArticle(mArticles.get(position));
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }


    class HotArticleVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;

        private Article mArticle;

        public HotArticleVH(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.article_title);

            itemView.setOnClickListener(this);

        }

        public void setArticle(Article article) {
            mArticle = article;
        }

        public Article getArticle() {
            return mArticle;
        }

        @Override
        public void onClick(View v) {

            if (getArticle() == null)
                return;

            Intent intent = new Intent(mContext, ArticleContentActivity.class);
            intent.putExtra(Constants.Key.ARTICLE, getArticle());

            Activity activity = null;
            if (mContext instanceof Activity)
                activity = (Activity) mContext;

            if (activity == null)
                return;

            if (activity instanceof BaseFragment.OnArticleSelectedListener)
                ((BaseFragment.OnArticleSelectedListener) activity).onArticleSelectedListener(getArticle());

            activity.startActivityForResult(intent, Constants.Code.REQUEST_CODE);


        }
    }

}
