package com.ywwxhz.view;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entity.CommentItem;
import com.ywwxhz.lib.kits.NetKit;

import org.apache.http.Header;
import org.json.JSONObject;

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
                        NetKit.getInstance().setCommentAction("support", citem.getSid(), citem.getTid(), token, chandler);
                        break;
                    case R.id.comment_against:
                        action = AGAINST;
                        NetKit.getInstance().setCommentAction("against", citem.getSid(), citem.getTid(), token, chandler);
                        break;
                    case R.id.comment_report:
                        action = REPORT;
                        NetKit.getInstance().setCommentAction("report", citem.getSid(), citem.getTid(), token, chandler);
                        break;
                }
                return true;
            }
        });

    }

    private JsonHttpResponseHandler chandler = new JsonHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Toast.makeText(mContext, "操作失败", Toast.LENGTH_LONG).show();
            throwable.printStackTrace();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                if ("success".equals(response.getString("state"))) {
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
                    citem.setHasscored(true);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(mContext, actionString + "成功", Toast.LENGTH_SHORT).show();
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                onFailure(statusCode, headers, e, response);
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