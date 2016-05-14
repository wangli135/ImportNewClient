package importnew.importnewclient.parser;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import importnew.importnewclient.bean.Article;

/**
 * 解析资讯、Web、架构、基础技术、书籍、教程
 * Created by Xingfeng on 2016/4/29.
 */
public class ArticlesParser {

    public static List<Article> parserArtciles(String html) {

        long current = System.currentTimeMillis();

        List<Article> articles = new ArrayList<>();

        try {

            Pattern pattern = Pattern.compile("(<!-- BEGIN .post -->).*((<!-- END .post -->))+?", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(html);
            while (matcher.find()) {

                String temp = html.substring(matcher.start(), matcher.end());
                Article article = null;
                String[] arrays = temp.split("<!-- END .post -->");
                for (int i = 0; i < arrays.length; i++) {

                    article = new Article();

                    temp = arrays[i];
                    pattern = Pattern.compile("<a.*<img.*></a>");
                    matcher = pattern.matcher(temp);
                    while (matcher.find()) {

                        //文章日期、评论、简介
                        String left = temp.substring(matcher.end() - 1);
                        //文章标题、链接、图片链接
                        temp = temp.substring(matcher.start(), matcher.end());

                        //文章链接
                        pattern = Pattern.compile("href.*\\.html\"");
                        matcher = pattern.matcher(temp);
                        while (matcher.find()) {

                            article.setUrl(temp.substring(matcher.start() + 6, matcher.end() - 1));

                        }

                        //文章标题
                        pattern = Pattern.compile("title=\".*\">");
                        matcher = pattern.matcher(temp);
                        while (matcher.find()) {
                            article.setTitle(temp.substring(matcher.start() + 7, matcher.end() - 2));
                        }

                        //文章图片链接
                        pattern = Pattern.compile("src.*\\.((jpg)|(png)|(gif))\"");
                        matcher = pattern.matcher(temp);
                        while (matcher.find()) {
                            article.setImgUrl(temp.substring(matcher.start() + 5, matcher.end() - 1));
                        }

                        //文章日期、评论部分
                        pattern = Pattern.compile("<p><a.*</p>");
                        matcher = pattern.matcher(left);
                        while (matcher.find()) {
                            temp = left.substring(matcher.start(), matcher.end());
                            left = left.substring(matcher.end() - 1);
                        }

                        //文章日期
                        pattern = Pattern.compile("\\d{4}/\\d{2}/\\d{2}");
                        matcher = pattern.matcher(temp);
                        while (matcher.find()) {
                            article.setDate(temp.substring(matcher.start(), matcher.end()));
                        }

                        //评论数目
                        pattern = Pattern.compile("\\d+ 条评论");
                        matcher = pattern.matcher(temp);
                        while (matcher.find()) {
                            article.setCommentNum(temp.substring(matcher.start(), matcher.end()));
                        }

                        //文章简述
                        pattern = Pattern.compile("<p>[^<].+</p>");
                        matcher = pattern.matcher(left);
                        while (matcher.find()) {
                            temp = left.substring(matcher.start() + 3, matcher.end() - 4);
                            temp = temp.replaceAll("<span.*\">", "");
                            temp = temp.replaceAll("</a></span>", "");
                            article.setDesc(temp);
                        }
                        articles.add(article);
                        article = null;

                    }


                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return articles;
        }

        System.out.println("Regex: " + (System.currentTimeMillis() - current));

        return articles;
    }


}
