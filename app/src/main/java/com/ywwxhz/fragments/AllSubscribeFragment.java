package com.ywwxhz.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nolanlawson.supersaiyan.SectionedListAdapter;
import com.nolanlawson.supersaiyan.Sectionizer;
import com.nolanlawson.supersaiyan.widget.SuperSaiyanScrollView;
import com.ywwxhz.MyApplication;
import com.ywwxhz.activitys.TopicNewsListActivity;
import com.ywwxhz.adapters.TopicListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.TopicsHelper;
import com.ywwxhz.entitys.TopicItem;
import com.ywwxhz.lib.database.exception.DbException;
import com.ywwxhz.lib.kits.Toolkit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/3 17:08.
 */
public class AllSubscribeFragment extends Fragment {
    private android.widget.ListView list;
    private com.nolanlawson.supersaiyan.widget.SuperSaiyanScrollView scroll;
    private SectionedListAdapter<TopicListAdapter> sectionedAdapter;
    private SubscribeHostFragment hostFragment;
    private TopicListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_subscribe, container, false);
        this.scroll = (SuperSaiyanScrollView) view.findViewById(R.id.scroll);
        this.list = (ListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new TopicListAdapter(getActivity(), new ArrayList<TopicItem>());
        adapter.setCallBack(new TopicListAdapter.onClickCallBack() {
            @Override
            public void onClick(TopicListAdapter adapter, TopicItem item) {
                item.setSaved(!item.isSaved());
                sectionedAdapter.notifyDataSetChanged();
                scroll.refresh();
                try {
                    MyApplication.getInstance().getDbUtils().saveOrUpdate(item);
                    Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT).show();
                } catch (DbException e) {
                    Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                if (hostFragment != null) {
                    hostFragment.notifySubscribed();
                }

            }
        });
        sectionedAdapter = SectionedListAdapter.Builder.create(getActivity(), adapter)
                .setSectionizer(new Sectionizer<TopicItem>() {

                    @Override
                    public CharSequence toSection(TopicItem input) {
                        return input.getLatter();
                    }
                })
                .sortKeys()
                .sortValues(new Comparator<TopicItem>() {

                    public int compare(TopicItem left, TopicItem right) {
                        int result = left.getLatter().compareToIgnoreCase(right.getLatter());
                        if (result == 0) {
                            return left.getTopicName().compareToIgnoreCase(right.getTopicName());
                        }
                        return result;
                    }
                })
                .build();
        list.setAdapter(sectionedAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), TopicNewsListActivity.class);
                intent.putExtra(TopicNewsListActivity.TPOIC_KEY, (TopicItem) sectionedAdapter.getItem(position));
                getActivity().startActivity(intent);
            }
        });
        Toolkit.runInUIThread(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        },200);
    }

    private void loadData() {
        List<TopicItem> countries = TopicsHelper.readLocalTopicList();
        adapter.setDataSet(countries);
        sectionedAdapter.notifyDataSetChanged();
    }
    private void getHostFragment() {
        if (getParentFragment() != null && getParentFragment() instanceof SubscribeHostFragment) {
            hostFragment = (SubscribeHostFragment) getParentFragment();
        }
    }
}
