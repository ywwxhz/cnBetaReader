package com.ywwxhz.fragments;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;

/**
 * cnBetaReader
 * <p>
 * Created by 远望の无限(ywwxhz) on 2016 2016/12/26 10:31.
 */

public abstract class BaseFragment extends Fragment {

    /**
     * 按键事件
     *
     * @param keyCode
     *            按键编号
     * @param event
     *            按键事件
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
