package com.ywwxhz.widget;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.CommentItem;
import com.ywwxhz.fragments.AddNewCommentFragment;
import com.ywwxhz.lib.handler.BaseJsonCallback;
import com.ywwxhz.lib.kits.NetKit;

import org.json.JSONObject;

import okhttp3.Response;

public class ExtendPopMenu extends PopupMenu {
    public int SUPPORT = 1;
    public int AGAINST = 2;
    public int REPORT = 3;
    private int action;
    private CommentItem citem;
    private Context mContext;
    private BaseAdapter adapter;
    private String token;

    public ExtendPopMenu(Context context, View anchor) {
        super(context, anchor);
        this.mContext = context;
        inflate(R.menu.menu_comment);
        setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.comment_support:
                        action = SUPPORT;
                        NetKit.setCommentAction(mContext, "support", citem.getSid() + "", citem.getTid(), token, chandler);
                        break;
                    case R.id.comment_against:
                        action = AGAINST;
                        NetKit.setCommentAction(mContext, "against", citem.getSid() + "", citem.getTid(), token, chandler);
                        break;
                    case R.id.comment_report:
                        action = REPORT;
                        NetKit.setCommentAction(mContext, "report", citem.getSid() + "", citem.getTid(), token, chandler);
                        break;
                    case R.id.comment_replay:
                        if (mContext instanceof Activity) {
                            AddNewCommentFragment fragment = AddNewCommentFragment.getInstance(citem.getSid(), citem.getTid(), token);
                            fragment.show(((Activity) mContext).getFragmentManager(), "new comment");
                        } else {
                            Toast.makeText(mContext, "function not impletment", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });

    }

    private BaseJsonCallback chandler = new BaseJsonCallback() {
        @Override
        protected void onError(int httpCode, Response response, Exception cause) {
            Toast.makeText(mContext, "操作失败", Toast.LENGTH_LONG).show();
            if (cause != null)
                cause.printStackTrace();
        }

        @Override
        protected void onResponse(JSONObject jsonObject) {
            try {
                if ("success".equals(jsonObject.getString("state"))) {
                    String actionString;
                    if (action == SUPPORT) {
                        actionString = "支持";
                        citem.setScore(citem.getScore() + 1);
                    } else if (action == AGAINST) {
                        actionString = "反对";
                        citem.setReason(citem.getReason() + 1);
                    } else {
                        actionString = "举报";
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(mContext, actionString + "成功", Toast.LENGTH_SHORT).show();
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                onError(200, null, e);
            }
        }
    };

    public void setCitem(CommentItem citem) {
        this.citem = citem;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
    }
}