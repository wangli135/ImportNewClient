package importnew.importnewclient.presenter;

import java.util.List;

import importnew.importnewclient.bean.Article;
import importnew.importnewclient.parser.ArticlesParser;
import importnew.importnewclient.parser.MontHotArticlesParser;
import importnew.importnewclient.view.IHotArticleView;

/**
 * Created by Xingfeng on 2017-02-15.
 */

public class MonthHotArticlePresenter extends HotArticlePresenter {

    public MonthHotArticlePresenter(IHotArticleView mHotArticleView) {
        super(mHotArticleView);
    }

    @Override
    public List<Article> parserHotArticles(String html) {
        ArticlesParser montHotArticlesParser = new MontHotArticlesParser();
        List<Article> articles = montHotArticlesParser.parser(html);
        return articles;
    }
}
