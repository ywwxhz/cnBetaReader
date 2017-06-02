package com.ywwxhz.activitys;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.CroutonStyle;
import com.ywwxhz.lib.ScrollToTopCliclListiner;
import com.ywwxhz.lib.ThemeManger;
import com.ywwxhz.widget.TranslucentStatus.TranslucentStatusHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * 含有 ToolBar 的 Activity
 */
public abstract class BaseToolBarActivity extends AppCompatActivity implements ScrollToTopCliclListiner {
    protected TranslucentStatusHelper helper;
    protected FrameLayout content;
    protected Toolbar toolbar;
    protected int colorPrimary;
    protected int colorPrimaryDark;
    protected int colorAccent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (shouldChangeTheme()) {
            ThemeManger.onActivityCreateSetTheme(this);
        }
        super.onCreate(savedInstanceState);
        super.setContentView(getBasicContentLayout());
        TypedArray array = obtainStyledAttributes(
                new int[] { R.attr.colorPrimary, R.attr.colorPrimaryDark, R.attr.colorAccent });
        colorPrimary = array.getColor(0, 0xFF1473AF);
        colorPrimaryDark = array.getColor(1, 0xFF11659A);
        colorAccent = array.getColor(2, 0xFF3C69CE);
        CroutonStyle.buildStyleInfo(colorPrimaryDark);
        CroutonStyle.buildStyleConfirm(colorAccent);
        array.recycle();
        helper = TranslucentStatusHelper.from(this).setStatusView(findViewById(R.id.statusView))
                .setActionBarSizeAttr(R.attr.actionBarSize).setStatusColor(colorPrimaryDark)
                .setTranslucentProxy(TranslucentStatusHelper.TranslucentProxy.AUTO).builder();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        content = (FrameLayout) findViewById(R.id.content);
    }

    @Override
    public void setContentView(int layoutResID) {
        content.removeAllViews();
        getLayoutInflater().inflate(layoutResID, content);
    }

    @Override
    public void setContentView(View view) {
        content.removeAllViews();
        content.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        content.removeAllViews();
        content.addView(view, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清空关联到此 Activity 的通知条
        Crouton.clearCroutonsForActivity(this);
    }

    /**
     * 获取基本的布局
     * 
     * @return 布局ID
     */
    protected int getBasicContentLayout() {
        return R.layout.activity_basetoolbar;
    }

    /**
     * 是否允许使用主题
     * 
     * @return
     */
    protected boolean shouldChangeTheme() {
        return true;
    }

    /**
     * 获取根视图
     * 
     * @return 根节点视图
     */
    protected FrameLayout getRootView() {
        return content;
    }

    @Override
    public void attachCallBack(View.OnClickListener onClickListener) {
        toolbar.setOnClickListener(onClickListener);
    }
}
