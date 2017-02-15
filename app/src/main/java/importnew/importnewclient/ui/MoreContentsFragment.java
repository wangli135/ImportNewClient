package importnew.importnewclient.ui;


import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.net.URLManager;

/**
 * "更多内容"页面，包含Android、IOS、Web和Python四个页面
 * A simple {@link Fragment} subclass.
 */
public class MoreContentsFragment extends FragmentsGroup {

    @Override
    public int getPageTitleResourceId() {
        return R.array.moreContentPageTitle;
    }

    @Override
    public List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ArticleListFragment.newInstance(URLManager.MORE_ANDROID));
        fragments.add(ArticleListFragment.newInstance(URLManager.MORE_IOS));
        fragments.add(ArticleListFragment.newInstance(URLManager.MORE_WEB));
        fragments.add(ArticleListFragment.newInstance(URLManager.MORE_PYTHON));
        return fragments;
    }

}
