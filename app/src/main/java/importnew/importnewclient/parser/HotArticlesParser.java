package importnew.importnewclient.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import importnew.importnewclient.bean.Article;
import importnew.importnewclient.utils.Constants;

/**
 * 热门文章解析
 * Created by Xingfeng on 2016/5/21.
 */
public class HotArticlesParser {

    public List<Article> parserArticle(String html) {
        List<Article> articles = new ArrayList<>();

        Pattern pattern = Pattern.compile("<a.+?(</a>)");
        Matcher matcher = pattern.matcher(html);
        Pattern innerPattern = null;
        Matcher innerMatcher = null;
        String s = "";
        String left = "";
        Article article = null;
        while (matcher.find()) {
            article = new Article();
            s = html.substring(matcher.start(), matcher.end());
            innerPattern = Pattern.compile(Constants.Regex.HOME_ARTICLE_URL);
            innerMatcher = innerPattern.matcher(s);
            while (innerMatcher.find()) {
                article.setUrl(s.substring(innerMatcher.start() + 6, innerMatcher.end() - 1));
                left = s.substring(innerMatcher.end());
            }


            innerPattern = Pattern.compile(Constants.Regex.HOME_ARTICLE_TITLE);
            innerMatcher = innerPattern.matcher(left);
            while (innerMatcher.find()) {
                article.setTitle(left.substring(innerMatcher.start() + 7, innerMatcher.end() - 2));
            }

            articles.add(article);
            article = null;

        }

        return articles;
    }

}
