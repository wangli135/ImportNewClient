package importnew.importnewclient.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;

/**
 * 显示文章详情的页面
 */
public class ArticleContentActivity extends AppCompatActivity {

    public static final String ARTICLE_KEY="article_key";

    private Article mArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content);
        mArticle= (Article) getIntent().getSerializableExtra(ARTICLE_KEY);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mArticle.getTitle());

    }
}
