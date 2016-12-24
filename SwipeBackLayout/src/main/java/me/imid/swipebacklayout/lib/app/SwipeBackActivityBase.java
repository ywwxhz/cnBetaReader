package me.imid.swipebacklayout.lib.app;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * @author Yrom
 */
public interface SwipeBackActivityBase {
    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    SwipeBackLayout getSwipeBackLayout();

    /**
     * 是否启用滑动返回
     * 
     * @param enable
     *            true 启用 false 禁用
     */
    void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    void scrollToFinishActivity();

}
