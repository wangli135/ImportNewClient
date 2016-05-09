package importnew.importnewclient.parser;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.bean.Article;
import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.pages.Nodes;

/**
 * 解析Importnew首页
 * Created by Xingfeng on 2016/4/28.
 */
public class HomePagerParser {

    /**
     * 解析首页
     *
     * @param homePageContent
     * @return 文章块集合
     */
    public static List<ArticleBlock> paserHomePage(String homePageContent) {


        List<ArticleBlock> lists = new ArrayList<>();
        ArticleBlock articleBlock = null;

        try{
            //解析HTML
            Document homepage = Jsoup.parse(homePageContent);
            //文章正文主体部分

            Element container = homepage.body().getElementById(Nodes.Id.WRAPPER).getElementsByTag(Nodes.Tag.DIV).first()
                    .getElementsByTag(Nodes.Tag.DIV).first().getElementById(Nodes.Id.HOMEPAGE_ARTICE);

            //解析推荐阅读子结点
            parserRecommendRead(lists, container.getElementById(Nodes.Id.RECOMMEND_READ));

            //解析最新文章和Java干货子节点
            parserLatestAndJava(lists, container.getElementById(Nodes.Id.LATEST_JAVA));

            //解析业界动态和最新技术
            parserNewsAndTech(lists, container.getElementById(Nodes.Id.NEWS_TECH));
        }catch (Exception e){
            e.printStackTrace();
            return lists;
        }



        return lists;
    }

    /**
     * 解析推荐阅读子结点
     *
     * @param recommendReadElemnt
     * @return 文章块
     */
    private static void parserRecommendRead(List<ArticleBlock> lists, Element recommendReadElemnt) {

        //标题
        Element title = recommendReadElemnt.getElementsByClass(Nodes.Class.H3_TITLE).first();
        Element container = recommendReadElemnt.getElementsByClass(Nodes.Class.CONTAINER).first();
        lists.add(parserTitleAndContainer(title, container));

    }


    /**
     * 解析最新文章和Java干货
     *
     * @param lists   文章块列表
     * @param element 最新文章和Java干货节点
     */
    private static void parserLatestAndJava(List<ArticleBlock> lists, Element element) {

        Element container = element.getElementsByClass(Nodes.Class.CONTAINER).first();
        for (Element grid_4 : container.children()) {

            if (grid_4.className().equals(Nodes.Class.GIRD_4)) {

                lists.add(parserGrid_4(grid_4));

            }

        }
    }

    /**
     * 解析业界动态和最新技术
     *
     * @param lists   文章块列表
     * @param element 业界动态和最新技术节点
     */
    private static void parserNewsAndTech(List<ArticleBlock> lists, Element element) {

        Elements titles = element.getElementsByClass(Nodes.Class.H3_TITLE);
        Elements containers = element.getElementsByClass(Nodes.Class.CONTAINER);

        for (int i = 0; i < titles.size(); i++) {
            lists.add(parserTitleAndContainer(titles.get(i), containers.get(i)));
        }

    }


    /**
     * 解析Class为grid-4的标签节点，对应垂直布局的文章
     *
     * @param grid_4
     * @return
     */
    private static ArticleBlock parserGrid_4(Element grid_4) {

        ArticleBlock block = new ArticleBlock();
        List<Article> lists = new ArrayList<>();
        Article article = null;
        //解析标题
        Element widgetTitle = grid_4.getElementsByClass(Nodes.Class.H3_TITLE).first();
        block.setCategory(widgetTitle.text());

        Element post_thumb=null;
        Element post_meta=null;
        //解析5篇文章
        for (Element arc : grid_4.children()) {
            if (arc.className().equals(Nodes.Class.FLOATED_THUMB)) {

                post_thumb=arc.getElementsByClass(Nodes.Class.ARTICLE_PIC).first();
                post_meta=arc.getElementsByClass(Nodes.Class.ARTICLE_METADATA).first();
                article=parserArticleHorizontal(post_thumb,post_meta);
                lists.add(article);
            }
        }

        block.setArticles(lists);
        return block;
    }

    /**
     * 解析widget-title和container节点
     *
     * @param title     文章分类节点
     * @param container 5篇文章节点
     * @return 文章块
     */
    private static ArticleBlock parserTitleAndContainer(Element title, Element container) {
        ArticleBlock articleBlock = new ArticleBlock();
        List<Article> arctiles = new ArrayList<>();
        Article article = null;

        articleBlock.setCategory(title.text());

        Elements post_thumbs=null;
        Elements post_metas=null;
        //5篇文章
        for (Element element : container.children()) {

            if (element.className().equals(Nodes.Class.FIRST_ARTICLE)) {
                article = parserArticleVertical(element);
                arctiles.add(article);
            } else if (element.className().equals(Nodes.Class.NOT_FIRST_ARCTILE)) {

                post_thumbs=element.getElementsByClass(Nodes.Class.ARTICLE_PIC);
                post_metas=element.getElementsByClass(Nodes.Class.ARTICLE_METADATA);
                for(int i=0;i<post_thumbs.size();i++){
                    arctiles.add(parserArticleHorizontal(post_thumbs.get(i),post_metas.get(i)));
                }

            } else {
                continue;
            }

        }

        articleBlock.setArticles(arctiles);

        return articleBlock;
    }

    /**
     * 解析文章结构垂直，即图片位于文章标题上方
     *
     * @param element
     * @return
     */
    private static Article parserArticleVertical(Element element) {

        Article article = new Article();

        Element img = element.getElementsByClass(Nodes.Class.ARTICLE_PIC).first().getElementsByTag(Nodes.Tag.A).first()
                .getElementsByTag(Nodes.Tag.IMG).first();
        article.setImgUrl(img.attr(Nodes.Attribute.SRC));

        Element title = element.getElementsByClass(Nodes.Class.ARTICLE_TITLE).first().getElementsByTag(Nodes.Tag.A).first();
        article.setTitle(title.attr(Nodes.Attribute.TITLE));
        article.setUrl(title.attr(Nodes.Attribute.HREF));

        return article;
    }

    /**
     * 解析文章结构水平，即图片位于文章标题左方
     *
     * @param post_thumb 图标信息
     * @param post_meta  标题信息
     * @return
     */
    private static Article parserArticleHorizontal(Element post_thumb, Element post_meta) {

        Article article = new Article();

        //图片
        Element img = post_thumb.getElementsByTag(Nodes.Tag.A).first()
                .getElementsByTag(Nodes.Tag.IMG).first();
        article.setImgUrl(img.attr(Nodes.Attribute.SRC));

        //标题
        Element title = post_meta.getElementsByTag(Nodes.Tag.A).first();
        article.setTitle(title.attr(Nodes.Attribute.TITLE));
        article.setUrl(title.attr(Nodes.Attribute.HREF));

        return article;
    }


}
