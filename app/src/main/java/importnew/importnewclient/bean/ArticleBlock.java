package importnew.importnewclient.bean;

import java.util.List;

/**
 * 文章块，解析首页的结果
 * Created by Xingfeng on 2016/4/30.
 */
public class ArticleBlock {

    /**
     * 文章分类
     */
    private String category;

    /**
     * 文章集合
     */
    private List<Article> articles;


    public ArticleBlock() {
    }

    public ArticleBlock(String category, List<Article> articles) {
        this.category = category;
        this.articles = articles;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
