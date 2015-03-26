package com.ywwxhz.lib;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 20:04.
 */
public class NavigationDrawerManger {
    private List<String> titles = new ArrayList<>(6);
    private List<Fragment> fragments = new ArrayList<>(6);

    public Fragment getFragment(int pos) {
        return fragments.get(pos);
    }

    public List<String> getTitles() {
        return titles;
    }

    public String getTitle(int pos) {
        return titles.get(pos);
    }

    public void registerFragment(String title, Fragment fragment) {
        titles.add(title);
        fragments.add(fragment);
    }

    public void unRegisterFragment(Fragment fragment) {
        int index = fragments.indexOf(fragment);
        if (index != -1) {
            fragments.remove(index);
            titles.remove(index);
        }
    }
}
