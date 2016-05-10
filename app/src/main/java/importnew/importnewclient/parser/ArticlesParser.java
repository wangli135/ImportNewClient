package importnew.importnewclient.parser;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import importnew.importnewclient.bean.Article;
import importnew.importnewclient.pages.Nodes;

/**
 * 解析资讯、Web、架构、基础技术、书籍、教程
 * Created by Xingfeng on 2016/4/29.
 */
public class ArticlesParser {

    public static List<Article> parserArtciles(String html){

        List<Article> lists=new LinkedList<>();
        Article article=null;

        try{
            Element grid_8 = Jsoup.parse(html).body().getElementById(Nodes.Id.WRAPPER).getElementsByClass(Nodes.Class.GRID_8).first();

            Elements post_floated_thumb=grid_8.getElementsByTag(Nodes.Tag.DIV);

            for(Element floated_thumb:post_floated_thumb){

                if(floated_thumb.className().equals(Nodes.Class.POST_FLOATED_THUMB)) {

                    article=parserArticle(floated_thumb);
                    lists.add(article);

                }else if(floated_thumb.className().equals(Nodes.Class.NAVIGATION)){
                    break;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            return lists;
        }
        return lists;
    }

    private static  Article parserArticle(Element floated_thumb){

        Article article=new Article();

        for(Node childNode:floated_thumb.childNodes()){
            if(childNode instanceof Element){
                Element element=(Element)childNode;

                if(element.className().equals(Nodes.Class.ARTICLE_PIC)){
                    Element img=element.getElementsByTag(Nodes.Tag.A).first().getElementsByTag(Nodes.Tag.IMG).first();
                    article.setImgUrl(img.attr(Nodes.Attribute.SRC));
                }else if(element.className().equals(Nodes.Class.ARTICLE_METADATA)){
                    Element p=element.getElementsByTag(Nodes.Tag.P).first();
                    Element title=p.getElementsByTag(Nodes.Tag.A).first();
                    article.setUrl(title.attr(Nodes.Attribute.HREF));
                    article.setTitle(title.attr(Nodes.Attribute.TITLE));

                    String text=p.text();
                    String right=text.substring(article.getTitle().length());
                    String[] array=right.split("\\|");
                    try {
                        article.setDate(new SimpleDateFormat("yyyy/MM/dd").parse(array[0].trim()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    Element span=floated_thumb.getElementsByClass(Nodes.Class.ARTICLE_METADATA).first().getElementsByTag(Nodes.Tag.SPAN).first();
                    article.setDesc(span.getElementsByTag(Nodes.Tag.P).first().text());
                }
            }
        }

        return article;
    }

}
