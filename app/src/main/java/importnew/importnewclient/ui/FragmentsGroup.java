package importnew.importnewclient.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import importnew.importnewclient.R;

/**
 * 包含多个文章列表的Fragment的父类,内部可以包含Fragment
 * Created by Xingfeng on 2017-02-15.
 */
public abstract class FragmentsGroup extends BaseFragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<Fragment> fragments;
    private String[] pageTitles;

    private FragmentStatePagerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_articles, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mTabLayout.setTabMode(getTabMode());
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        initDatas();
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initDatas() {

        pageTitles = getResources().getStringArray(getPageTitleResourceId());
        fragments = getFragments();
        mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return pageTitles[position];
            }
        };

    }

    /**
     * 返回TabLayout的模式，默认为固定尺寸
     *
     * @return
     */
    public int getTabMode() {
        return TabLayout.MODE_FIXED;
    }

    /**
     * 获取PageTitle资源Id
     *
     * @return
     */
    public abstract int getPageTitleResourceId();

    /**
     * 获取内部包含的Fragment
     *
     * @return
     */
    public abstract List<Fragment> getFragments();
}
