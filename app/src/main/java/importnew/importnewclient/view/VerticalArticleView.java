package importnew.importnewclient.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.ui.ArticleContentActivity;


/**
 * 垂直布局的文章视图，顶部为文章图片，底部为文章标题
 * Created by Xingfeng on 2016/4/30.
 */
public class VerticalArticleView extends FrameLayout{

    private LayoutInflater mInfalter;

    private ImageView mArticleImg;
    private TextView mArticleTitle;
    private Article mArticle;//关联的文章

    public VerticalArticleView(Context context) {
        this(context,null);
    }

    public VerticalArticleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VerticalArticleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInfalter=LayoutInflater.from(context);
        View rootView=mInfalter.inflate(R.layout.article_vertical_layout,this);
        mArticleImg=(ImageView)rootView.findViewById(R.id.article_img);
        mArticleTitle=(TextView)rootView.findViewById(R.id.article_title);
    }

    public void setImageResource(int resId){
        mArticleImg.setImageResource(resId);
    }

    public void setImageBitmap(Bitmap bm){
        mArticleImg.setImageBitmap(bm);
    }

    public void setText(CharSequence text){
        mArticleTitle.setText(text);
    }

    public Drawable getDrawable(){
        return mArticleImg.getDrawable();
    }

    public CharSequence getText(){
        return mArticleTitle.getText();
    }

    public ImageView getImageView(){
        return mArticleImg;
    }

    public Article getArticle() {
        return mArticle;
    }

    public void setArticle(Article mArticle) {
        this.mArticle = mArticle;
    }

    /**
     * 设置点击跳转事件
     * @param startable
     */
    public void setStartActivity(boolean startable){
        if(startable){
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mArticle!=null){
                        Intent intent=new Intent(v.getContext(), ArticleContentActivity.class);
                        intent.putExtra(ArticleContentActivity.ARTICLE_KEY,mArticle);
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}
