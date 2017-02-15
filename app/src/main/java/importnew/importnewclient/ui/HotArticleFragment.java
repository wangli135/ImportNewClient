package importnew.importnewclient.ui;


import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;

/**
 * 热门文章Fragment，包含两个子Fragment，月热门和年热门
 * A simple {@link Fragment} subclass.
 */
public class HotArticleFragment extends FragmentsGroup {

    @Override
    public int getPageTitleResourceId() {
        return R.array.hotArticlesPageTitle;
    }

    @Override
    public List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MonthHotFragment());
        fragments.add(new YearHotFragment());
        return fragments;
    }

}
