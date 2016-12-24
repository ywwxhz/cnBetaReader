package com.ywwxhz.hoderview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.CommentItem;
import com.ywwxhz.lib.SpannableStringUtils;
import com.ywwxhz.lib.ThemeManger;
import com.ywwxhz.lib.handler.BaseJsonCallback;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.UIKit;
import com.ywwxhz.widget.ExtendPopMenu;
import com.ywwxhz.widget.textdrawable.TextDrawable;
import com.ywwxhz.widget.textdrawable.util.ColorGenerator;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * CnbetaReader
 * com.ywwxhz.hoder
 * Created by 远望の无限(ywwxhz) on 2015/2/3 9:49.
 */
public class NewsCommentItemHoderView extends RelativeLayout implements View.OnClickListener {

    public int SUPPORT = 1;
    public int AGAINST = 2;
    private int action;
    private TextView comment_name;
    private TextView comment_ref;
    private TextView comment_content;
    private TextView comment_reason;
    private TextView comment_score;
    private TextView comment_time;
    private View comment_more;
    private ExtendPopMenu popMenu;
    private ImageView comment_image;
    private TextView comment_from;
    private boolean showEmoji;
    private CommentItem item;
    private String token;
    private BaseAdapter adapter;
    private OnClickListener listiner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(comment_more.getVisibility()==VISIBLE) {
                if (v == comment_score) {
                    action = SUPPORT;
                    NetKit.setCommentAction(getContext(), "support", item.getSid() + "", item.getTid(), token, chandler);
                } else {
                    action = AGAINST;
                    NetKit.setCommentAction(getContext(), "against", item.getSid() + "", item.getTid(), token, chandler);
                }
            }
        }
    };

    private BaseJsonCallback chandler = new BaseJsonCallback() {

        /**
         * 对返回数据进行操作的回调， UI线程
         *
         * @param jsonObject
         * @param call
         * @param response
         */
        @Override
        public void onSuccess(JSONObject jsonObject, Call call, Response response) {
            try {
                if ("success".equals(jsonObject.getString("state"))) {
                    OkGo.getInstance().getDelivery().post(new Runnable() {
                        @Override
                        public void run() {
                            String actionString;
                            if (action == SUPPORT) {
                                actionString = "支持";
                                item.setScore(item.getScore() + 1);
                            } else if (action == AGAINST) {
                                actionString = "反对";
                                item.setReason(item.getReason() + 1);
                            } else {
                                actionString = "举报";
                            }
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), actionString + "成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    throw new Exception(jsonObject.toString());
                }
            } catch (Exception e) {
                System.out.println(jsonObject);
                onError(200, response, e);
            }
        }

        public void onError(int httpCode, Response response, Exception cause) {
            OkGo.getInstance().getDelivery().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "操作失败", Toast.LENGTH_LONG).show();
                }
            });
            if (cause != null){
                cause.printStackTrace();
            }
        }

        /**
         * 调用成功回调<br/>
         * 用于兼容旧版接口，如使用该方法请不要覆写 onSuccess(T t, Call call, Response response)
         *
         * @param jsonObject
         */
        @Override
        protected void onResponse(JSONObject jsonObject) {
            // do nothing
        }
    };

    public NewsCommentItemHoderView(Context context) {
        super(context);
    }

    public NewsCommentItemHoderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsCommentItemHoderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.comment_name = (TextView) findViewById(R.id.comment_name);
        this.comment_ref = (TextView) findViewById(R.id.comment_ref);
        this.comment_content = (TextView) findViewById(R.id.comment_content);
        this.comment_reason = (TextView) findViewById(R.id.comment_reason);
        this.comment_score = (TextView) findViewById(R.id.comment_score);
        this.comment_time = (TextView) findViewById(R.id.comment_time);
        this.comment_image = (ImageView) findViewById(R.id.comment_image);
        this.comment_more = findViewById(R.id.comment_more);
        this.comment_from = (TextView) findViewById(R.id.comment_from);
        this.popMenu = new ExtendPopMenu(getContext(), comment_more);
        showEmoji = PrefKit.getBoolean(getContext(), R.string.pref_show_emoji_key, true);
        if (ThemeManger.isNightTheme(getContext())) {
            this.comment_ref.setBackgroundResource(R.drawable.ref_background_night);
        } else {
            this.comment_ref.setBackgroundResource(R.drawable.ref_background);
        }
        Drawable[] reasonDrawables = comment_reason.getCompoundDrawables();
        reasonDrawables[0] = UIKit.tintDrawable(reasonDrawables[0], comment_reason.getCurrentTextColor());
        comment_reason.setCompoundDrawables(reasonDrawables[0], null, null, null);
        Drawable[] scoreDrawables = comment_score.getCompoundDrawables();
        scoreDrawables[0] = UIKit.tintDrawable(scoreDrawables[0], comment_score.getCurrentTextColor());
        comment_score.setCompoundDrawables(scoreDrawables[0], null, null, null);
        comment_score.setOnClickListener(listiner);
        comment_reason.setOnClickListener(listiner);
    }

    public void showComment(CommentItem item, String token, BaseAdapter adapter, boolean enable, TextDrawable.IBuilder drawableBuilder, ColorGenerator colorGenerator) {
        comment_name.setText(item.getName());
        this.token = token;
        this.item = item;
        this.adapter = adapter;
        if (item.getRefContent().length() != 0) {
            if (comment_ref.getVisibility() == GONE) {
                comment_ref.setVisibility(View.VISIBLE);
            }
            if (showEmoji) {
                comment_ref.setText(SpannableStringUtils.span(getContext(), Html.fromHtml(item.getRefContent()).toString()));
            } else {
                comment_ref.setText(Html.fromHtml(item.getRefContent()).toString());
            }
        } else {
            if (comment_ref.getVisibility() == VISIBLE) {
                comment_ref.setVisibility(View.GONE);
            }
        }
        comment_image.setImageDrawable(drawableBuilder.build(String.valueOf(item.getName().charAt(0)), colorGenerator.getColor(item.getTid())));
        if (showEmoji) {
            comment_content.setText(SpannableStringUtils.span(getContext(), Html.fromHtml(item.getComment()).toString()));
        } else {
            comment_content.setText(Html.fromHtml(item.getComment()).toString());
        }
        comment_time.setText(item.getDate());
        comment_from.setText(item.getHost_name());
        comment_score.setText(item.getScore() > 999 ? "999+" : item.getScore() + "");
        comment_reason.setText(item.getReason() > 999 ? "999+" : item.getReason() + "");
        if (enable) {
            if (comment_more.getVisibility() == GONE) {
                comment_more.setVisibility(VISIBLE);
            }
            comment_more.setOnClickListener(this);
            popMenu.setCitem(item);
            popMenu.setToken(token);
        } else {
            if (comment_more.getVisibility() == VISIBLE) {
                comment_more.setVisibility(GONE);
            }
            comment_more.setOnClickListener(null);
        }
    }

    @Override
    public void onClick(View v) {
        popMenu.show();
    }
}
