package importnew.importnewclient.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 使用正则表达式从文章HTML文档中删除无用部分
     *
     * @param html 文章HTML文档
     * @return 删除后的文档
     */
    public static String parserArticleBody(String html) {

        StringBuilder sb = new StringBuilder();

        //提取header部分
        Pattern pattern = Pattern.compile("<!DOCTYPE.+</head>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        String left = null;
        while (matcher.find()) {
           // System.out.println(html.substring(matcher.start(), matcher.end()));
            sb.append(html.substring(0, matcher.end()));
            left = html.substring(matcher.end());
        }

        //删除头部
        pattern = Pattern.compile("<!-- BEGIN header -->.+<!-- END header -->", Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        //删除entery-meta部分
        pattern = Pattern.compile("<!-- BEGIN \\.entry-meta -->.+<!-- END \\.entry-meta -->", Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        //提取文章末尾分享、广告部分
        pattern = Pattern.compile("<!-- JiaThis Button BEGIN -->.+<!-- END \\.post -->", Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        //删除评论部分
        pattern = Pattern.compile("<!-- BEGIN #respond -->.+<!-- END \\.navigation -->", Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        //删除sidebar部分
        pattern = Pattern.compile("<!-- BEGIN #sidebar -->.+<!-- END #sidebar -->", Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        //删除footer部分
        pattern = Pattern.compile("<!-- BEGIN footer -->.+<!-- END footer -->", Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        sb.append(left);

        return sb.toString();
    }

}
