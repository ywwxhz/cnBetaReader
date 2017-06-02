package com.ywwxhz.processers;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ywwxhz.data.BaseDataProvider;

/**
 *
 *
 * @param <E>
 * @param <DataProvider>
 */
public interface BaseProcesser<E, DataProvider extends BaseDataProvider<E>> {

    /**
     * 对应生命周期中的 onResume
     */
    void onResume();

    /**
     * 对应生命周期中的 onPause
     */
    void onPause();

    /**
     * 对应生命周期中的 onDestroy
     */
    void onDestroy();

    /**
     * 关联视图
     * 
     * @param view
     *            视图
     */
    void assumeView(View view);

    /**
     * 加载数据
     * 
     * @param startup
     *            是否初次加载
     */
    void loadData(boolean startup);

    /**
     * 获取Activity
     * 
     * @return 关联的Activity
     */
    AppCompatActivity getActivity();

    /**
     * 设置关联的Activity
     * 
     * @param activity
     *            关联的Activity
     */
    void setActivity(AppCompatActivity activity);

    /**
     * 设置数据提供者
     * 
     * @param provider
     *            数据提供者
     */
    void setProvider(DataProvider provider);

    /**
     * 菜单选择选中事件
     * 
     * @param item
     *            菜单项
     * @return
     */
    boolean onOptionsItemSelected(MenuItem item);

    /**
     * 对应生命周期中的 onConfigurationChanged
     * 
     * @param newConfig
     */
    void onConfigurationChanged(Configuration newConfig);

    /**
     * 对应生命周期中的 onCreateOptionsMenu
     * 
     * @param menu
     *            菜单项
     * @param inflater
     */
    void onCreateOptionsMenu(Menu menu, MenuInflater inflater);

    /**
     *  对应生命周期中的 setUserVisibleHint
     * @param isVisibleToUser
     */
    void setUserVisibleHint(boolean isVisibleToUser);
}
