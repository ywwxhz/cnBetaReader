package com.ywwxhz.data;

import com.ywwxhz.MyApplication;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.TopicItem;
import com.ywwxhz.lib.database.exception.DbException;
import com.ywwxhz.lib.kits.LogKits;
import com.ywwxhz.lib.kits.PrefKit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TopicsHelper {
    private final static int CURRENT_VERSION = 0;

    public static List<TopicItem> readLocalTopicList() {
        List<TopicItem> items;
        try {
            if(MyApplication.getInstance().getDbUtils().count(TopicItem.class) > 0) {
                LogKits.d("load topic from db");
                return MyApplication.getInstance().getDbUtils().findAll(TopicItem.class);
            }else{
                LogKits.d("load topic from file");
                items = new ArrayList<>(300);
                BufferedReader buff = null;
                try {
                    buff = new BufferedReader(new InputStreamReader(MyApplication.getInstance().getResources().openRawResource(R.raw.topic_all)
                            , Charset.forName("utf-8")));
                    PrefKit.writeInt(MyApplication.getInstance(), "topic_version", CURRENT_VERSION);
                    while (buff.ready()) {
                        String[] line = buff.readLine().split("\t");
                        TopicItem item = new TopicItem();
                        item.setLatter(line[0]);
                        item.setTopicId(line[1]);
                        item.setTopicName(line[2]);
                        item.setTopicImage(line[3]);
                        items.add(item);
                    }
                    LogKits.d("save topic to db");
                    final List<TopicItem> finalItems = items;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MyApplication.getInstance().getDbUtils().saveAll(finalItems);
                            } catch (DbException ignored) {
                            }
                        }
                    }).start();
                    LogKits.d("load topic success");
                } catch (IOException e) {
                    LogKits.d("load topic failure");
                    e.printStackTrace();
                } finally {
                    if (buff != null) {
                        try {
                            buff.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
            }

        } catch (DbException e) {
            LogKits.d("load topic failure");
            e.printStackTrace();
            items = new ArrayList<>();
        }

        return items;
    }
}
