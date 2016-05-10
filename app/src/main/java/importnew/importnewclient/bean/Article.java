package importnew.importnewclient.bean;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章实体类
 * Created by Xingfeng on 2016/4/30.
 */
public class Article implements Serializable{

    /**
     * 文章URL
     */
    private String url;

    /**
     * 文章图片URL
     */
    private String imgUrl;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章简介
     */
    private String desc;

    /**
     * 文章内容
     */
    private ArticleBody body;

    /**
     * 图片
     */
    private Bitmap bitmap;

    /**
     * 评论数目
     */
    private int commentNum;

    /**
     * 文章日期
     */
    private Date date;

    /**
     * 是否是收藏的文章
     */
    private boolean isFavourite;



    public Article() {
    }

    public Article(String url, String imgUrl, String title, String desc, ArticleBody body, Bitmap bitmap, int commentNum, Date date) {
        this.url = url;
        this.imgUrl = imgUrl;
        this.title = title;
        this.desc = desc;
        this.body = body;
        this.bitmap = bitmap;
        this.commentNum = commentNum;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArticleBody getBody() {
        return body;
    }

    public void setBody(ArticleBody body) {
        this.body = body;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    /**
     * 两篇文章的URL相同，则认为文章相等
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        return url.equals(article.url);

    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
