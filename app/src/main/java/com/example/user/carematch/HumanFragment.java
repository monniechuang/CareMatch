package com.example.user.carematch;
//jason
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class HumanFragment extends Fragment {


    View HumanFragmentview;

    //分類
    private TabLayout tabLayout;
    private ViewPager firstViewPager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        HumanFragmentview = inflater.inflate(R.layout.fragment_human, container, false);

        //分類
        firstViewPager = (ViewPager) HumanFragmentview.findViewById(R.id.viewpager_content);
        //設定tab
        tabLayout = (TabLayout) HumanFragmentview.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(firstViewPager);
        setupViewPager(firstViewPager);


        return HumanFragmentview;
    }


    //收藏分類
    private void setupViewPager(ViewPager viewPager) {


        HumanFragment.Adapter adapter = new HumanFragment.Adapter(getChildFragmentManager());
        adapter.addFragment(new HumanList(), "條件搜尋");
        adapter.addFragment(new HumanSearchList(), "快速搜尋");
        adapter.addFragment(new HumanMathList(), "精準推薦");

        viewPager.setAdapter(adapter);


    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}