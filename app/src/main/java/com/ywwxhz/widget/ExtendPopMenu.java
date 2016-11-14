package com.ywwxhz.widget;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
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
    private Context mContext;
    private String token;
    private int sid;
    private String tid;

    public ExtendPopMenu(Context context, View anchor) {
        super(context, anchor);
        this.mContext = context;
        inflate(R.menu.menu_comment);
        setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.comment_report:
                        NetKit.setCommentAction(mContext, "report", sid + "", tid, token, chandler);
                        break;
                    case R.id.comment_replay:
                        if (mContext instanceof Activity) {
                            AddNewCommentFragment fragment = AddNewCommentFragment.getInstance(sid, tid, token);
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

        protected void onError(int httpCode, Response response, Exception cause) {
            Toast.makeText(mContext, "操作失败", Toast.LENGTH_LONG).show();
            if (cause != null)
                cause.printStackTrace();
        }

        protected void onResponse(JSONObject jsonObject) {
            try {
                if ("success".equals(jsonObject.getString("state"))) {
                    Toast.makeText(mContext, "举报成功", Toast.LENGTH_SHORT).show();
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                onError(200, null, e);
            }
        }
    };

    public void setCitem(CommentItem citem) {
        sid = citem.getSid();
        tid = citem.getTid();
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}