package com.ywwxhz.data.impl;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.adapters.CommentListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.entitys.CommentItem;
import com.ywwxhz.entitys.CommentListObject;
import com.ywwxhz.entitys.ResponseObject;
import com.ywwxhz.lib.handler.BaseResponseObjectResponse;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.Toolkit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.keyboardsurfer.android.widget.crouton.Style;
import okhttp3.Response;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/4 20:44.
 */
public class NewsCommentProvider extends ListDataProvider<CommentItem, CommentListAdapter> {
    private int sid;
    private String sn;
    private String token;
    private TextView message;
    private View listView;
    private View mSwipeLayout;
    private final BaseResponseObjectResponse handler = new BaseResponseObjectResponse<CommentListObject>(new TypeToken<ResponseObject<CommentListObject>>() {
    }) {

        @Override
        protected String beforeConvertSuccess(String body) throws Exception {
            // 针对 cnBeta 新版 如果没有热门评论就会加载精彩评论造成程序异常，通过此方法替换此项为空列表
            // 2017-02-23
            JSONObject object = new JSONObject(body);
            try{
                object.getJSONObject("result").getJSONArray("hotlist");
            }catch (JSONException e){
                Log.e("error",body);
                object.getJSONObject("result").put("hotlist", new JSONArray());
            }
            return object.toString();
        }

        @Override
        protected void onSuccess(CommentListObject result) {
            callOnLoadingSuccess(result, false, false);
            System.out.println(result.getCmntlist());
        }

        @Override
        protected Activity getActivity() {
            return NewsCommentProvider.this.getActivity();
        }


        @Override
        protected void onError(int httpCode, Response response, Throwable cause) {
            if (!callOnFailure(false, false)) {
                super.onError(httpCode, response, cause);
            }
        }

        @Override
        public void onFinish() {
            if (callback != null)
                callback.onLoadFinish(1);
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
        NetKit.getCommentBySnAndSid(getActivity(), sn, sid + "", handler);
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
                FileCacheKit.getInstance().putAsync(sid + "", Toolkit.getGson().toJson(commentListObject), "comment", null);
            }
        } else if (commentListObject.getOpen() == 0) { //针对关平的新闻评论
            this.mSwipeLayout.setEnabled(false);
            if (callOnFailure(false, true)) {
                this.message.setText(R.string.message_comment_close);
                this.listView.setVisibility(View.GONE);
                this.message.setVisibility(View.VISIBLE);
            }
        } else {//针对暂时无评论的情况
            if (getAdapter().getCount() != 0) {
                this.listView.setVisibility(View.GONE);
            }
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
            } else
                return !isCommentClose;
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
