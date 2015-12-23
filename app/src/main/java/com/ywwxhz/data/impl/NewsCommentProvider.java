package com.ywwxhz.data.impl;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ywwxhz.adapters.CommentListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.entitys.CommentItem;
import com.ywwxhz.entitys.CommentListObject;
import com.ywwxhz.entitys.ResponseObject;
import com.ywwxhz.lib.CroutonStyle;
import com.ywwxhz.lib.handler.BaseHttpResponseHandler;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.Toolkit;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/4 20:44.
 */
public class NewsCommentProvider extends ListDataProvider<CommentItem, CommentListAdapter> {
    private int sid;
    private String sn;
    private String token;
    private View actionButton;
    private TextView message;
    private View listView;
    private View mSwipeLayout;
    private final AsyncHttpResponseHandler handler = new BaseHttpResponseHandler<CommentListObject>(new TypeToken<ResponseObject<CommentListObject>>() {
    }) {
        @Override
        protected void onSuccess(CommentListObject result) {
            callOnLoadingSuccess(result, false, false);
        }

        @Override
        protected Activity getActivity() {
            return NewsCommentProvider.this.getActivity();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (!callOnFailure(false, false)) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        }

        @Override
        protected void onError(int statusCode, Header[] headers, String responseString, Throwable cause) {
            super.onError(statusCode, headers, responseString, cause);
            callOnFailure(true, true);
        }

        @Override
        public void onFinish() {
            if (callback != null) callback.onLoadFinish(1);
        }
    };

    public NewsCommentProvider(Activity activity) {
        super(activity);
    }

    @Override
    protected CommentListAdapter newAdapter() {
        return new CommentListAdapter(getActivity(), new ArrayList<CommentItem>(20));
    }

    @Override
    public String getTypeKey() {
        return null;
    }

    @Override
    public String getTypeName() {
        return "全部评论";
    }

    @Override
    public void loadNewData() {
        NetKit.getInstance().getCommentBySnAndSid(sn, sid + "", handler);
    }

    @Override
    public void loadNextData() {

    }

    @Override
    public AdapterView.OnItemLongClickListener getOnItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, getAdapter().getDataSetItem(position - 1).getComment()));
                Toast.makeText(getActivity(), "评论已复制到剪贴板", Toast.LENGTH_SHORT).show();
                return true;
            }
        };
    }

    @Override
    public void loadData(boolean startup) {
        loadNewData();
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private void callOnLoadingSuccess(CommentListObject commentListObject, boolean fromCache, boolean isClosed) {
        this.token = commentListObject.getToken();
        this.getAdapter().setToken(token);
        ArrayList<CommentItem> cmntlist = commentListObject.getCmntlist();
        HashMap<String, CommentItem> cmntstore = commentListObject.getCmntstore();
        for (CommentItem item : cmntlist) {
            StringBuilder sb = new StringBuilder();
            item.copy(cmntstore.get(item.getTid()));
            CommentItem parent = cmntstore.get(item.getPid());
            while (parent != null) {
                sb.append("//@");
                sb.append(parent.getName());
                sb.append(": [");
                sb.append(parent.getHost_name());
                sb.append("] <br/>");
                sb.append(parent.getComment());
                parent = cmntstore.get(parent.getPid());
                if (parent != null) {
                    sb.append("<br/>");
                }
            }
            item.setRefContent(sb.toString());
        }
        ArrayList<CommentItem> hotcmntlist = commentListObject.getHotlist();
        for (CommentItem item : hotcmntlist) {
            StringBuilder sb = new StringBuilder();
            item.copy(cmntstore.get(item.getTid()));
            CommentItem parent = cmntstore.get(item.getPid());
            while (parent != null) {
                sb.append("//@");
                sb.append(parent.getName());
                sb.append(": [");
                sb.append(parent.getHost_name());
                sb.append("] <br/>");
                sb.append(parent.getComment());
                parent = cmntstore.get(parent.getPid());
                if (parent != null) {
                    sb.append("<br/>");
                }
            }
            item.setRefContent(sb.toString());
        }
        if (cmntlist.size() > 0) { //针对加载缓存和普通访问
            this.getAdapter().setDataSet(cmntlist);
            this.getAdapter().setHotComment(hotcmntlist);
            if (!isClosed && !fromCache) {
                this.getAdapter().setEnable(true);
                this.actionButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        actionButton.setVisibility(View.VISIBLE);
                        actionButton.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    }
                }, 200);
                FileCacheKit.getInstance().putAsync(sid + "", Toolkit.getGson().toJson(commentListObject), "comment", null);
                Toolkit.showCrouton(getActivity(), R.string.message_flush_success, CroutonStyle.INFO);
            } else {
                this.getAdapter().setEnable(false);
            }
        } else if (commentListObject.getOpen() == 0) { //针对关平的新闻评论
            Toolkit.showCrouton(getActivity(), R.string.message_comment_close, Style.ALERT);
            this.getAdapter().setEnable(false);
            this.mSwipeLayout.setEnabled(false);
            if (callOnFailure(false, true)) {
                this.message.setText(R.string.message_comment_close);
                this.listView.setVisibility(View.GONE);
                this.message.setVisibility(View.VISIBLE);
            }
        } else {//针对暂时无评论的情况
            Toolkit.showCrouton(getActivity(), R.string.message_no_comment, CroutonStyle.INFO);
            if (getAdapter().getCount() != 0) {
                this.listView.setVisibility(View.GONE);
            }
            this.actionButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                    actionButton.setVisibility(View.VISIBLE);
                    actionButton.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                }
            }, 200);
            this.message.setText(R.string.message_no_comment);
            this.message.setVisibility(View.VISIBLE);
        }
    }

    private boolean callOnFailure(boolean isWebChange, boolean isCommentClose) {
        CommentListObject commentListObject = FileCacheKit.getInstance().getAsObject(sid + "", "comment", new TypeToken<CommentListObject>() {
        });
        if (commentListObject != null) {
            callOnLoadingSuccess(commentListObject, true, isCommentClose);
            if (!isWebChange && !isCommentClose) {
                Toolkit.showCrouton(getActivity(), R.string.message_load_from_cache, Style.ALERT);
                return true;
            } else return !isCommentClose;
        } else {
            if (!isCommentClose) {
                this.message.setText(R.string.message_no_network);
                this.message.setVisibility(View.VISIBLE);
                return false;
            } else {
                return true;
            }
        }
    }

    public void setActionButton(View actionButton) {
        this.actionButton = actionButton;
    }

    public void setListView(View listView) {
        this.listView = listView;
    }

    public void setMessage(TextView message) {
        this.message = message;
    }

    public void setSwipeLayout(View mSwipeLayout) {
        this.mSwipeLayout = mSwipeLayout;
    }
}
