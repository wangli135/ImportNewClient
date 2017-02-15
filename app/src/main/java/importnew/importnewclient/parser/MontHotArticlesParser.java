package importnew.importnewclient.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import importnew.importnewclient.bean.Article;

/**
 * 解析本月热门文章
 * Created by Xingfeng on 2017-02-14.
 */
public class MontHotArticlesParser extends HotArticlesParser implements ArticlesParser {

    @Override
    public List<Article> parser(String html) {
        List<Article> articles = new ArrayList<>();
        Article article = null;

        Pattern pattern = Pattern.compile("id=\"tab-most-discussed.+?(id)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        String s = "";
        while (matcher.find()) {
            s = html.substring(matcher.start(), matcher.end());
            return parserArticle(s);
        }

        return articles;
    }
}
