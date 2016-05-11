package importnew.importnewclient.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * 文章实体类
 * Created by Xingfeng on 2016/4/30.
 */
public class Article implements Parcelable{

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
     * 文章对应html文本
     */
    private String bodyString;

    protected Article(Parcel in) {
        url = in.readString();
        imgUrl = in.readString();
        title = in.readString();
        desc = in.readString();
        bodyString = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        commentNum = in.readInt();
        isFavourite = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(imgUrl);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(bodyString);
        dest.writeParcelable(bitmap, flags);
        dest.writeInt(commentNum);
        dest.writeByte((byte) (isFavourite ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getBodyString() {
        return bodyString;
    }

    public void setBodyString(String bodyString) {
        this.bodyString = bodyString;
    }

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

    public Article(String url, String imgUrl, String title, String desc, String bodyString, Bitmap bitmap, int commentNum, Date date, boolean isFavourite) {
        this.url = url;
        this.imgUrl = imgUrl;
        this.title = title;
        this.desc = desc;
        this.bodyString = bodyString;
        this.bitmap = bitmap;
        this.commentNum = commentNum;
        this.date = date;
        this.isFavourite = isFavourite;
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
