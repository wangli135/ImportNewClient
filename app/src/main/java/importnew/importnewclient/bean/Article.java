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
    private String body;

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


    public Article() {
    }

    public Article(String url, String imgUrl, String title, String desc, String body, Bitmap bitmap, int commentNum, Date date) {
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if (url != null ? !url.equals(article.url) : article.url != null) return false;
        if (title != null ? !title.equals(article.title) : article.title != null) return false;
        if (desc != null ? !desc.equals(article.desc) : article.desc != null) return false;
        if (body != null ? !body.equals(article.body) : article.body != null) return false;
        return date != null ? date.equals(article.date) : article.date == null;

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
