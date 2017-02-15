package importnew.importnewclient.presenter;

import java.util.List;

import importnew.importnewclient.bean.Article;
import importnew.importnewclient.parser.ArticlesParser;
import importnew.importnewclient.parser.YearHotArticlesParser;
import importnew.importnewclient.view.IHotArticleView;

/**
 * Created by Xingfeng on 2017-02-15.
 */

public class YearHotArticlePresenter extends HotArticlePresenter {

    public YearHotArticlePresenter(IHotArticleView mHotArticleView) {
        super(mHotArticleView);
    }

    @Override
    public List<Article> parserHotArticles(String html) {
        ArticlesParser yearHotArticlesParser = new YearHotArticlesParser();
        List<Article> articles = yearHotArticlesParser.parser(html);
        return articles;
    }
}
