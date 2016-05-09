package importnew.importnewclient.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import importnew.importnewclient.bean.ArticleBody;
import importnew.importnewclient.pages.Nodes;

/**
 * 解析一篇文章
 * Created by Xingfeng on 2016/5/5.
 */
public class ArticleBodyParser {

    public static ArticleBody parser(String html) {

        ArticleBody articleBody = new ArticleBody();
        try {
            Element grid_8 = Jsoup.parse(html).getElementById(Nodes.Id.WRAPPER).getElementsByClass(Nodes.Class.GRID_8).first();

            //解析文章主体部分
            for (Element element : grid_8.children()) {

                //文章主体部分
                if (element.tagName().equals(Nodes.Tag.DIV) && (element.id().startsWith("post"))) {

                    for (Element element1 : element.children()) {

                        //标题
                        if (element1.className().equals(Nodes.Class.ENTRY_HEADER)) {
                            articleBody.addEntryHeader(element1);
                        }
                        //文章正文
                        else if (element1.className().equals(Nodes.Class.ENTRY)) {
                            articleBody.addEntry(element1);
                        }

                    }


                }
                //评论部分
                else if (element.tagName().equals(Nodes.Tag.DIV) && element.id().equals(Nodes.Id.RESPOND)) {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return articleBody;
        }

        return articleBody;
    }
}
