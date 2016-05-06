package importnew.importnewclient.parser;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

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
        for (Element element : grid_8.children()) {

            //文章主体部分
            if (element.tagName().equals(Nodes.Tag.DIV) && (element.id().startsWith("post"))) {

                for (Element element1 : element.children()) {

                    //标题
                    if (element1.className().equals(Nodes.Class.ENTRY_HEADER)) {
                        addTitle(articleBody, element1);
                    }
                    //文章正文
                    else if (element1.className().equals(Nodes.Class.ENTRY)) {
                        parserArticleContent(element1, articleBody);
                    }

                }


            }
            //评论部分
            else if (element.tagName().equals(Nodes.Tag.DIV) && element.id().equals(Nodes.Id.RESPOND)) {

            }

        }

        return articleBody;

    }

    /**
     * 解析文章主体部分
     *
     * @param parent      父节点
     * @param articleBody 文章主体部分
     */
    private static void parserArticleContent(Element parent, ArticleBody articleBody) {

        ArticleBody.Node node = null;

        for (Node childNode : parent.childNodes()) {

            if(childNode instanceof Element){

                Element ele=(Element)childNode;
                if (ele.className().equals(Nodes.Class.COPYRIGHT)) {
                    node = new ArticleBody.Node(Tag.TEXT, ele.text());
                    articleBody.add(node);
                } else if (ele.id().equals(Nodes.Id.ARTICLE_CONTENT)) {

                    for (Element element1 : ele.children()) {

                        if (element1.tagName().equals(Nodes.Tag.DIV)) {

                            parserArticleContent(element1, articleBody);

                            break;
                        }

                    }
                    break;

                } else if (ele.tagName().equals(Nodes.Tag.P)&&ele.childNodeSize()>1) {

                    parserArticleTagOfP(ele,articleBody);

                }  else if(ele.tagName().equals(Nodes.Tag.P)){
                    parserArticleContent(ele,articleBody);
                }
                else if (ele.tagName().equals(Nodes.Tag.H2)) {
                    node = new ArticleBody.Node(Tag.H2, ele.text());
                    articleBody.add(node);
                } else if (ele.tagName().equals(Nodes.Tag.H3)) {
                    node = new ArticleBody.Node(Tag.H3, ele.text());
                    articleBody.add(node);
                } else if (ele.tagName().equals(Nodes.Tag.H1)) {
                    node = new ArticleBody.Node(Tag.H1, ele.text());
                    articleBody.add(node);
                } else if (ele.tagName().equals(Nodes.Tag.BR)) {
                    node = new ArticleBody.Node(Tag.BR, "");
                    articleBody.add(node);
                } else if (ele.tagName().equals(Nodes.Tag.STRONG)) {
                    node = new ArticleBody.Node(Tag.STRONG, ele.text());
                    articleBody.add(node);
                } else if (ele.tagName().equals(Nodes.Tag.A)&&ele.hasText()) {
                    node = new ArticleBody.Node(Tag.A, ele.text());
                    node.setUrl(ele.attr(Nodes.Attribute.HREF));
                    articleBody.add(node);
                }else if(ele.tagName().equals(Nodes.Tag.A)){
                    parserArticleContent(ele,articleBody);
                }else if(ele.tagName().equals(Nodes.Tag.IMG)){
                    node=new ArticleBody.Node(Tag.IMG,ele.text());
                    node.setUrl(ele.attr(Nodes.Attribute.SRC));
                    articleBody.add(node);
                }

            }else if(childNode instanceof TextNode){

                TextNode textNode=(TextNode)childNode;
                if(!TextUtils.isEmpty(textNode.text().trim())) {
                    node=new ArticleBody.Node(Tag.TEXT,textNode.text());
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

    /**
     * 解析标签P
     * @param tagOfP
     * @param articleBody
     */
    private static void parserArticleTagOfP(Element tagOfP,ArticleBody articleBody){

        ArticleBody.Node nodeP=new ArticleBody.Node(Tag.P,tagOfP.text());
        ArticleBody.Node childNode=null;
        for(Node node:tagOfP.childNodes()){

            if(node instanceof TextNode){

                TextNode textNode=(TextNode)node;
                if(!(TextUtils.isEmpty(textNode.text().trim()))){
                    childNode=new ArticleBody.Node(Tag.TEXT,textNode.text());
                    nodeP.add(childNode);
                }

            }else if(node instanceof Element){

                Element element=(Element)node;

                if(element.tagName().equals(Nodes.Tag.STRONG)){
                    childNode=new ArticleBody.Node(Tag.STRONG,element.text());
                    nodeP.add(childNode);
                }else if(element.tagName().equals(Nodes.Tag.A)){
                    childNode=new ArticleBody.Node(Tag.A,element.text());
                    childNode.setUrl(element.attr(Nodes.Attribute.HREF));
                    nodeP.add(childNode);
                }else if(element.tagName().equals(Nodes.Tag.IMG)){
                    childNode=new ArticleBody.Node(Tag.IMG,element.text());
                    childNode.setUrl(element.attr(Nodes.Attribute.SRC));
                    nodeP.add(childNode);
                }else if(element.tagName().equals(Nodes.Tag.BR)){
                    childNode=new ArticleBody.Node(Tag.BR,"");
                    nodeP.add(childNode);
                }else{
                    childNode=new ArticleBody.Node(Tag.TEXT,element.text());
                    nodeP.add(childNode);
                }



            }


        }

        articleBody.add(nodeP);

    }
}
