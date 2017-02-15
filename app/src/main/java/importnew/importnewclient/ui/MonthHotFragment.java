package importnew.importnewclient.ui;


import importnew.importnewclient.presenter.HotArticlePresenter;
import importnew.importnewclient.presenter.MonthHotArticlePresenter;
import importnew.importnewclient.view.IHotArticleView;

/**
 * 本月热门文章
 */
public class MonthHotFragment extends AbstractHotFragment implements IHotArticleView {

    @Override
    public HotArticlePresenter getHotArticlePresenter() {
        return new MonthHotArticlePresenter(this);
    }
}
