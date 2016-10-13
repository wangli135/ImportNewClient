package importnew.importnewclient.utils;

/**
 * 常量值类
 * Created by Xingfeng on 2016/5/10.
 */
public class Constants {

    public static boolean IS_SAVE_FLOW = false;

    /**
     * 键
     */
    public static class Key {

        public static final String SELECTION = "listview_selection";
        /**
         * 文章
         */
        public static final String ARTICLE = "article";

        public static final String ARTICLE_URL = "article_url";
        /**
         * 文章主体内容
         */
        public static final String ARTICLE_BODY = "article_body";

        /**
         * 是否收藏
         */
        public static final String IS_FAVOURITE = "is_favourite";

        /**
         * 文章列表
         */
        public static final String ARTICLE_LIST = "article_list";

        /**
         * 文章页数
         */
        public static final String PAGE_NUM = "page_num";

        public static final String ARTICLE_BASE_URL = "article_base_url";

        public static final String NUM_OF_FRAGMENT = "num_of_fragment";

        public static final String UPDATE_INFO = "updateInfo";

        public static final String PICTURE_URL = "picture_url";

        /**
         * 省流量模式
         */
        public static String IS_SAVE_FLOW_MODE = "is_save_flow_mode";

        /**
         * 夜间模式
         */
        public static String IS_NIGHT_MODE = "is_night_mode";
    }


    /**
     * 状态码
     */
    public static class Code {

        public static final int REQUEST_CODE = 24;

    }


    /**
     * 正则表达式标签
     */
    public static class Regex {

        /**
         * 首页文章主体
         */
        public static final String HOME_ARTICLE = "<a\\s+target.*href.*title.*<img.*></a>";

        /**
         * 首页文章链接
         */
        public static final String HOME_ARTICLE_URL = "href.+\\.html\"";

        /**
         * 首页文章标题
         */
        public static final String HOME_ARTICLE_TITLE = "title.*\">";

        /**
         * 首页文章图片链接
         */
        public static final String HOME_ARTICLE_IMG = "src.*\\.((jpg)|(png)|(gif)|(jpeg))";

        /**
         * 文章列表文章主体
         */
        public static final String LIST_ARTICLES_BODY = "(<!-- BEGIN .post -->).*((<!-- END .post -->))+?";

        /**
         * 文章列表分隔符
         */
        public static final String LIST_ARTICLES_SPLIT = "<!-- END .post -->";

        public static final String LIST_ARTICLES_IMG_BLOCK = "<a.*<img.*></a>";
        public static final String LIST_ARTICLES_URL = "href=\".+?\"";
        public static final String LIST_ARTICLES_TITLE = "title=\\\".*\\\">";
        public static final String LIST_ARTICLES_IMG = "src=\".+?(\")";
        public static final String LIST_ARTICLES_COMMENT_DATE = "<p><a.*?</p>";
        public static final String LIST_ARTICLES_DATE = "\\d{4}/\\d{1,2}/\\d{1,2}";
        public static final String LIST_ARTICLES_COMMENT = "\\d+ 条评论";
        public static final String LIST_ARTICLES_DESC = "<p>[^<].+</p>";

        /**
         * 头部
         */
        public static final String BODY_HEAD = "<!DOCTYPE.+</head>";


        /**
         * 删除的头部
         */
        public static final String BODY_DELETE_HEAD = "<!-- BEGIN header -->.+<!-- END header -->";


        /**
         * 删除entry-meta
         */
        public static final String BODY_DELETE_ENTRY_META = "<!-- BEGIN \\.entry-meta -->.+<!-- END \\.entry-meta -->";


        /**
         * 删除文章末尾广告部分
         */
        public static final String BODY_DELETE_AD = "<!-- JiaThis Button BEGIN -->.+<!-- END \\.post -->";

        /**
         * 删除评论部分
         */
        public static final String BODY_DELETE_COMMENTS = "<!-- BEGIN #respond -->.+<!-- END \\.navigation -->";

        /**
         * 删除文章Sidebar
         */
        public static final String BODY_DELETE_SIDEBAR = "<!-- BEGIN #sidebar -->.+<!-- END #sidebar -->";


        /**
         * 删除文章footer
         */
        public static final String BODY_DELETE_FOOTER = "<!-- BEGIN footer -->.+<!-- END footer -->";

        /**
         * 删除文章top-nav节点
         */
        public static final String BODY_DELETE_TOPNAV = "<nav id.+<!-- END #top-nav -->";

        /**
         * 删除文章打赏部分
         */
        public static final String BODY_DELETE_REWARDS = "<blockquote class=\"rewards\".*</blockquote>.*<!-- BEGIN #author-bio -->";

        /**
         * ImportNew文章标识
         */
        public static final String IS_ARTICLE_URL = "http.+((importnew)|(jobbole))\\.com/\\d{2,}+";

        /**
         * ImportNew图片标识
         */
        public static final String IS_PICTURE_URL = "http.+\\.((png)|(jpg)|(jpeg)|(gif))";
    }
}
