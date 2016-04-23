package com.ywwxhz.activitys;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ywwxhz.MyApplication;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.TopicItem;
import com.ywwxhz.fragments.TopicNewsFragment;
import com.ywwxhz.lib.CroutonStyle;
import com.ywwxhz.lib.database.exception.DbException;
import com.ywwxhz.lib.kits.Toolkit;

import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/4 11:52.
 */
public class TopicNewsListActivity extends ExtendBaseActivity {
    public static final String TPOIC_KEY = "key_topic";
    private TopicItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getIntent().getExtras().containsKey(TPOIC_KEY)) {
            this.finish();
            return;
        }
        item = (TopicItem) getIntent().getSerializableExtra(TPOIC_KEY);
        getSupportActionBar().setTitle(item.getTopicName());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content
                    , TopicNewsFragment.getInstance(item)).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        } else if (item.getItemId() == R.id.menu_subscribe) {
            try {
                this.item.setSaved(!this.item.isSaved());
                MyApplication.getInstance().getDbUtils().saveOrUpdate(this.item);
                invalidateOptionsMenu();
                Toolkit.showCrouton(this, "操作成功", CroutonStyle.INFO);
            } catch (DbException e) {
                Toolkit.showCrouton(this, "操作失败", Style.ALERT);
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_list, menu);
        if (item.isSaved()) {
            menu.findItem(R.id.menu_subscribe).setTitle("取消关注");
        } else {
            menu.findItem(R.id.menu_subscribe).setTitle("关注");
        }
        return super.onCreateOptionsMenu(menu);
    }
}
