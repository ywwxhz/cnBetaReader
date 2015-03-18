package com.ywwxhz.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.processer.NewsListProcesser;


public class NewsListActivity extends BaseActivity {

    private NewsListProcesser mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        getActionBar().setDisplayShowHomeEnabled(true);
        mService = new NewsListProcesser(this);
        ListView list = mService.getListView();
        option.setConfigView(list);
        fixPos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mService.onResume();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixPos();
    }

    private void fixPos() {
        int[] ints = helper.getInsertPixs(false);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mService.getFloatButtom().getLayoutParams();
        int margin = layoutParams.leftMargin;
        layoutParams.rightMargin = margin + ints[2];
        layoutParams.bottomMargin = margin + ints[3];
        mService.getFloatButtom().setLayoutParams(layoutParams);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mService.onReturn(requestCode, resultCode);
    }
}
