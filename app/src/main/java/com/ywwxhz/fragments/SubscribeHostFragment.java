package com.ywwxhz.fragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.widget.SlidingTabLayout.SlidingTabLayout;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/2 21:21.
 */
public class SubscribeHostFragment extends Fragment {
    private SlidingTabLayout slidingTabLayout;
    private ViewPager mPager;
    private String[] titles = {"已关注","可关注"};
    private Fragment[] fragments = {new SubscribedFragment(),new AllSubscribeFragment()};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_susbcribe_host, container, false);
        this.mPager = (ViewPager) view.findViewById(R.id.pager);
        this.slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        this.slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        TypedArray array = getActivity().getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimaryDark});
        this.slidingTabLayout.setSelectedIndicatorColors(array.getColor(0, getResources().getColor(R.color.statusColor)));
        array.recycle();
        this.slidingTabLayout.setDistributeEvenly(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mPager.setAdapter(new NavigationAdapter(getChildFragmentManager()));
        this.mPager.requestDisallowInterceptTouchEvent(true);
        this.slidingTabLayout.setViewPager(mPager);
    }

    public void notifySubscribed() {

    }

    private class NavigationAdapter extends FragmentPagerAdapter {

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
