package com.ywwxhz.activitys;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.ResponseHandlerInterface;
import com.ywwxhz.MyApplication;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.data.impl.NetNewsListDataProvider;
import com.ywwxhz.entitys.TopicItem;
import com.ywwxhz.fragments.BaseNewsListFragment;
import com.ywwxhz.lib.database.exception.DbException;
import com.ywwxhz.lib.kits.NetKit;
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
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new BaseNewsListFragment() {
                @Override
                public ListDataProvider getProvider() {
                    return new NetNewsListDataProvider(TopicNewsListActivity.this) {
                        @Override
                        public String getTypeKey() {
                            return item.getTopicId();
                        }

                        @Override
                        public String getTypeName() {
                            return item.getTopicName();
                        }

                        @Override
                        public void makeRequest(int page, String type, ResponseHandlerInterface handlerInterface) {
                            NetKit.getInstance().getNewslistByTopic(page, type, handlerInterface);
                        }

                        @Override
                        public int getPageSize() {
                            return 40;
                        }
                    };
                }

                @Override
                public boolean hasMenu() {
                    return true;
                }
            }).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }else if (item.getItemId() == R.id.menu_subscribe){
            try {
                this.item.setSaved(!this.item.isSaved());
                MyApplication.getInstance().getDbUtils().saveOrUpdate(this.item);
                invalidateOptionsMenu();
                Toolkit.showCrouton(this, "操作成功", Style.INFO);
            } catch (DbException e) {
                Toolkit.showCrouton(this,"操作失败", Style.ALERT);
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_list, menu);
        if(item.isSaved()) {
            menu.findItem(R.id.menu_subscribe).setTitle("取消关注");
        }else{
            menu.findItem(R.id.menu_subscribe).setTitle("关注");
        }
        return super.onCreateOptionsMenu(menu);
    }
}
