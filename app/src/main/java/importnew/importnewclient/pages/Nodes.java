package importnew.importnewclient.pages;

/**
 * 节点常量，包含id、class和html标签
 * Created by Xingfeng on 2016/4/28.
 */
public class Nodes {

    public static class Id{

        /**
         * 内容主体
         */
        public static  final String WRAPPER="wrapper";


        /**
         * 首页文章主体ID
         */
        public static final String HOMEPAGE_ARTICE="widgets-homepage-fullwidth";

        /**
         * 推荐阅读ID
         */
        public static final String RECOMMEND_READ="aw_popular_posts_widget-2";

        /**
         * 最新文章和Java干货ID
         */
        public static final String LATEST_JAVA="aw_latestfeaturedposts_widget-3";

        /**
         * 业界动态和基础技术ID
         */
        public static final String NEWS_TECH="aw_categories_widget-2";



    }

    public static class Class{

        /**
         * 标题Class
         */
        public static final String H3_TITLE="widget-title";

        /**
         * 内容Class
         */
        public static final String CONTAINER="container";

        /**
         * 推荐阅读、业界动态、基础技术第一篇文章Class(横向布局)
         */
        public static final String FIRST_ARTICLE="grid-4 the-latest";

        /**
         * 推荐阅读、业界动态、基础技术的非第一篇文章(横向布局)
         */
        public static final String NOT_FIRST_ARCTILE="grid-4 floated-thumb";

        /**
         * 垂直布局文章div的Class
         */
        public static final String FLOATED_THUMB="floated-thumb";

        /**
         * 所有文章中文章div的class
         *
         */
        public static final String POST_FLOATED_THUMB="post floated-thumb";

        /**
         * 文章图片Class
         */
        public static final String ARTICLE_PIC="post-thumb";

        /**
         * 文章标题Class
         */
        public static final String ARTICLE_TITLE="post-title";

        /**
         * 文章日期和评论数目
         */
        public static final String ARTICLE_METADATA="post-meta";


        public static final String GIRD_4="grid-4";

        public static final String GRID_8="grid-8";

        public static final String NAVIGATION="navigation margin-20";


    }

    public static class Tag{

        public static final String H3="h3";

        public static final String A="a";

        public static final String P="p";

        public static final String IMG="img";

        public static final String SPAN="span";

        public static final String DIV="div";

    }

    public static class Attribute{
        public static final String SRC="src";

        public static final String HREF="href";

        public static final String TITLE="title";
    }

}
