package importnew.importnewclient.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.net.URLManager;

/**
 * 所有文章页面
 * A simple {@link Fragment} subclass.
 */
public class AllArticlesFragment extends Fragment {

    private SmartTabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<ArticleListFragment> fragments;

   private FragmentStatePagerAdapter mAdapter;
    public AllArticlesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_articles, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabLayout=(SmartTabLayout)view.findViewById(R.id.viewpagertab);


        mViewPager=(ViewPager)view.findViewById(R.id.viewpager);
        initDatas();
        mViewPager.setAdapter(mAdapter);

        mTabLayout.setViewPager(mViewPager);

    }

    private void initDatas(){
        fragments=new ArrayList<>();
        fragments.add(ArticleListFragment.newInstance(URLManager.NEWS));
        fragments.add(ArticleListFragment.newInstance(URLManager.WEB));
        fragments.add(ArticleListFragment.newInstance(URLManager.ARCHITECUTRE));
        fragments.add(ArticleListFragment.newInstance(URLManager.BASIC));
        fragments.add(ArticleListFragment.newInstance(URLManager.BOOKS));
        fragments.add(ArticleListFragment.newInstance(URLManager.TUTORIAL));
        mAdapter=new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };

    }
}