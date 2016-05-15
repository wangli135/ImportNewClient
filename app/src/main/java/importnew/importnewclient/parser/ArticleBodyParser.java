package importnew.importnewclient.parser;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析一篇文章
 * Created by Xingfeng on 2016/5/5.
 */
public class ArticleBodyParser {

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
        String left = "";
        while (matcher.find()) {
           // System.out.println(html.substring(matcher.start(), matcher.end()));
            sb.append(html.substring(0, matcher.end()));
            left = html.substring(matcher.end());
        }

        //删除头部
        pattern = Pattern.compile("<!-- BEGIN header -->.+<!-- END header -->", Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start())).append("<br>");
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
