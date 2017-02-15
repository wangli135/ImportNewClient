package importnew.importnewclient.ui;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.net.URLManager;

/**
 * "所有文章"Fragment，包含所有文章、资讯、Web、架构、基础技术、书籍、教程各个分类
 * A simple {@link Fragment} subclass.
 */
public class AllArticlesFragment extends FragmentsGroup {

    @Override
    public int getTabMode() {
        return TabLayout.MODE_SCROLLABLE;
    }

    @Override
    public int getPageTitleResourceId() {
        return R.array.allArticlesPageTitle;
    }

    @Override
    public List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ArticleListFragment.newInstance(URLManager.ALL_POSTS));
        fragments.add(ArticleListFragment.newInstance(URLManager.NEWS));
        fragments.add(ArticleListFragment.newInstance(URLManager.WEB));
        fragments.add(ArticleListFragment.newInstance(URLManager.ARCHITECUTRE));
        fragments.add(ArticleListFragment.newInstance(URLManager.BASIC));
        fragments.add(ArticleListFragment.newInstance(URLManager.BOOKS));
        fragments.add(ArticleListFragment.newInstance(URLManager.TUTORIAL));
        return fragments;
    }
}
