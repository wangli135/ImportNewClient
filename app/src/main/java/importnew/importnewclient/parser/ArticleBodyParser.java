package importnew.importnewclient.parser;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import importnew.importnewclient.bean.ArticleBody;
import importnew.importnewclient.bean.Tag;
import importnew.importnewclient.pages.Nodes;

/**
 * 解析一篇文章
 * Created by Xingfeng on 2016/5/5.
 */
public class ArticleBodyParser {

    public static ArticleBody parser(String html) {

        ArticleBody articleBody = new ArticleBody();

        Element grid_8 = Jsoup.parse(html).body().getElementById(Nodes.Id.WRAPPER).getElementsByClass(Nodes.Class.GRID_8).first();

        //解析文章主体部分
        Element firstDiv = grid_8.getElementsByTag(Nodes.Tag.DIV).first();

        Elements divs = firstDiv.getElementsByTag(Nodes.Tag.DIV);
        for (Element element : divs) {

            //标题
            if (element.className().equals(Nodes.Class.ENTRY_HEADER)) {
                addTitle(articleBody, element);
            }
            //文章正文
            else if (element.className().equals(Nodes.Class.ENTRY)) {

                Elements elements = element.getAllElements();
                ArticleBody.Node node = null;

                parserArticleContent(elements, element, articleBody);

            }
        }


        Log.d("wangli", articleBody.toString());

        return articleBody;

    }

    private static void parserArticleContent(Elements elements, Element parent, ArticleBody articleBody) {

        ArticleBody.Node node=null;
        for (Element ele : elements) {

            if (ele.parent().equals(parent)) {

                if (ele.className().equals(Nodes.Class.COPYRIGHT)) {
                    node = new ArticleBody.Node(Tag.TEXT, ele.text());
                    articleBody.add(node);
                } else if (ele.id().equals(Nodes.Id.ARTICLE_CONTENT)) {

                    Element element=ele.getElementsByTag(Nodes.Tag.DIV).first().getElementsByTag(Nodes.Tag.DIV).first();
                    parserArticleContent(element.getAllElements(),element,articleBody);
                    break;

                } else if (ele.tagName().equals(Nodes.Tag.P) && ele.hasText()) {
                    node = new ArticleBody.Node(Tag.P, ele.text());
                    articleBody.add(node);
                } else if (ele.tagName().equals(Nodes.Tag.H2)) {
                    node = new ArticleBody.Node(Tag.H2, ele.text());
                    articleBody.add(node);
                } else if (ele.tagName().equals(Nodes.Tag.H3)) {
                    node = new ArticleBody.Node(Tag.H3, ele.text());
                    articleBody.add(node);
                } else if (ele.tagName().equals(Nodes.Tag.H1)) {
                    node = new ArticleBody.Node(Tag.H1, ele.text());
                    articleBody.add(node);
                }


            }


        }

    }

    /**
     * 添加文章头
     *
     * @param articleBody
     * @param title
     */
    private static void addTitle(ArticleBody articleBody, Element title) {

        ArticleBody.Node node = new ArticleBody.Node();
        node.setTag(Tag.H1);
        node.setText(title.getElementsByTag(Nodes.Tag.H1).first().text());
        articleBody.add(node);

    }

}
