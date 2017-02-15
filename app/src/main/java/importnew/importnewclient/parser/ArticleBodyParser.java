package importnew.importnewclient.parser;


import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import importnew.importnewclient.utils.Constants.Regex;

/**
 * 从html页面中解析出一篇文章
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

        if(TextUtils.isEmpty(html)){
            return "";
        }

        StringBuilder sb = new StringBuilder();

        //提取header部分
        Pattern pattern = Pattern.compile(Regex.BODY_HEAD, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        String left = "";
        while (matcher.find()) {
            sb.append(html.substring(0, matcher.end()));
            left = html.substring(matcher.end());
        }


        //删除top-nav节点
        pattern = Pattern.compile(Regex.BODY_DELETE_TOPNAV,Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start())).append("<br>");
            left = left.substring(matcher.end());
        }

        //删除头部
        pattern = Pattern.compile(Regex.BODY_DELETE_HEAD, Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        //删除entery-meta部分
        pattern = Pattern.compile(Regex.BODY_DELETE_ENTRY_META, Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        //删除伯乐在线文章的打赏部分,赞、收藏按钮
        pattern = Pattern.compile(Regex.BODY_DELETE_REWARDS, Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }


        //提取文章末尾分享、广告部分
        pattern = Pattern.compile(Regex.BODY_DELETE_AD, Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        //删除评论部分
        pattern = Pattern.compile(Regex.BODY_DELETE_COMMENTS, Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        //删除伯乐在线文章的底部登录部分
        pattern=Pattern.compile("<div id=\"article-comment\".+</div>",Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        //删除sidebar部分
        pattern = Pattern.compile(Regex.BODY_DELETE_SIDEBAR, Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        //删除footer部分
        pattern = Pattern.compile(Regex.BODY_DELETE_FOOTER, Pattern.DOTALL);
        matcher = pattern.matcher(left);
        while (matcher.find()) {
            sb.append(left.substring(0, matcher.start()));
            left = left.substring(matcher.end());
        }

        sb.append(left);

        return sb.toString();
    }

}
