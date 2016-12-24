package me.imid.swipebacklayout.lib.app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;

/**
 * @author Yrom
 */
public class SwipeBackActivityHelper {
    private Activity mActivity;

    private SwipeBackLayout mSwipeBackLayout;

    public SwipeBackActivityHelper(Activity activity) {
        mActivity = activity;
    }

    @SuppressWarnings("deprecation")
    public void onActivityCreate() {
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mActivity.getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(mActivity)
                .inflate(me.imid.swipebacklayout.lib.R.layout.swipeback_layout, null);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
                Utils.convertActivityToTranslucent(mActivity);
            }

            @Override
            public void onScrollOverThreshold() {

            }
        });
    }

    /**
     * 对应 Activity 生命周期中的 onPostCreate
     */
    public void onPostCreate() {
        mSwipeBackLayout.attachToActivity(mActivity);
        findViewById(android.R.id.content).setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 对应 Activity 生命周期中的 onPostCreate
     * 
     * @param shouldUseThemeBackground
     *            是否使用主题中的背景
     * @param backgroundRes
     *            背景资源
     */
    public void onPostCreate(boolean shouldUseThemeBackground, int backgroundRes) {
        mSwipeBackLayout.attachToActivity(mActivity, shouldUseThemeBackground, backgroundRes);
        findViewById(android.R.id.content).setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 通过控件 ID 找控件
     * 
     * @param id
     *            控件ID
     * @return
     */
    public View findViewById(int id) {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return null;
    }

    /**
     * 获取滑动返回控件
     * 
     * @return 滑动返回控件
     */
    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }
}
