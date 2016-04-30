package importnew.importnewclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.view.VerticalArticleView;

/**
 * Created by Xingfeng on 2016/4/30.
 */
public class ArticleBlockAdapter extends RecyclerView.Adapter<ArticleBlockAdapter.ArticleBlockVH> {

    private List<ArticleBlock> datas;

    public ArticleBlockAdapter(List<ArticleBlock> datas) {
        this.datas = datas;
    }

    @Override
    public ArticleBlockVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.articles_block_layout,parent,false);
        return new ArticleBlockVH(view);
    }

    @Override
    public void onBindViewHolder(ArticleBlockVH holder, int position) {

        ArticleBlock articleBlock=datas.get(position);
        holder.category.setText(articleBlock.getCategory());
        List<Article> articles=articleBlock.getArticles();
        Article article=null;
        VerticalArticleView verticalArticleView=null;
        for(int i=0;i<articles.size();i++){

            verticalArticleView=holder.views[i];
            article=articles.get(i);
            verticalArticleView.setText(article.getTitle());
            verticalArticleView.setImageBitmap(article.getBitmap());

        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ArticleBlockVH extends RecyclerView.ViewHolder{

        TextView category;
        VerticalArticleView[] views;


        public ArticleBlockVH(View itemView) {
            super(itemView);
            category=(TextView)itemView.findViewById(R.id.articles_category);
            views=new VerticalArticleView[5];
            views[0]= (VerticalArticleView) itemView.findViewById(R.id.first_article);
            views[1]= (VerticalArticleView) itemView.findViewById(R.id.second_article);
            views[2]= (VerticalArticleView) itemView.findViewById(R.id.third_article);
            views[3]= (VerticalArticleView) itemView.findViewById(R.id.fourth_article);
            views[4]= (VerticalArticleView) itemView.findViewById(R.id.fifth_article);

        }
    }

}
