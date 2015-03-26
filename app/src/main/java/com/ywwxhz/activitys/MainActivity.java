package com.ywwxhz.activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.fragments.NavigationDrawerFragment;

public class MainActivity extends BaseToolBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private int current = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
               R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(Fragment fragment,int pos) {
        if(fragment!=null&&current!=pos){
            getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
            current = pos;
        }
    }

    @Override
    protected int getBasicContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
        System.exit(0);
    }
}
