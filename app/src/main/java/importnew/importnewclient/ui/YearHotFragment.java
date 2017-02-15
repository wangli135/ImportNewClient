package importnew.importnewclient.ui;


import importnew.importnewclient.presenter.HotArticlePresenter;
import importnew.importnewclient.presenter.YearHotArticlePresenter;
import importnew.importnewclient.view.IHotArticleView;

/**
 * 年度热门文章
 */
public class YearHotFragment extends AbstractHotFragment implements IHotArticleView {

    @Override
    public HotArticlePresenter getHotArticlePresenter() {
        return new YearHotArticlePresenter(this);
    }
}
