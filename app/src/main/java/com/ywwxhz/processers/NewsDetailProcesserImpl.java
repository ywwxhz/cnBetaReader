package com.ywwxhz.processers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.ywwxhz.MyApplication;
import com.ywwxhz.activitys.ImageViewActivity;
import com.ywwxhz.activitys.NewsCommentActivity;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.fragments.FontSizeFragment;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.database.exception.DbException;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.Toolkit;
import com.ywwxhz.widget.TranslucentStatus.TranslucentStatusHelper;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Locale;
import java.util.regex.Matcher;

import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * cnBetaReader
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:48.
 */
public class NewsDetailProcesserImpl extends BaseProcesserImpl {
    public static final String NEWS_ITEM_KEY = "key_news_item";
    private final TranslucentStatusHelper helper;
    private View loadFail;
    private WebView mWebView;
    private ActionBarActivity mContext;
    private boolean hascontent;
    private NewsItem mNewsItem;
    private ProgressWheel mProgressBar;
    private FloatingActionButton mActionButtom;
    private VideoWebChromeClient client = new VideoWebChromeClient() ;

    private String webTemplate = "<!DOCTYPE html><html><head><title></title><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"/>" +
            "<style>body{word-break: break-all;}video{width:100%% !important;height:auto !important}.content img{max-width: 100%% !important;height: auto; !important}a{text-decoration: none;color:#2f7cad;}" +
            ".content p iframe{width: 100%% !important;height:auto !important}.introduce img{padding:3pt;width:50pt;height:auto}.introduce p{margin:0}.introduce div{margin: 0px !important;}" +
            ".content embed{width: 100%% !important;height:auto; !important}.title{font-size: 18pt;color: #1473af;}" +
            ".from{font-size: 10pt;padding-top: 4pt;}.introduce{border: 1px solid #E5E5E5;background-color: #FBFBFB;font-size: 11pt;padding: 2pt;}" +
            ".content{padding-top:10pt;font-size: 12pt;}.clear{clear: both;}.foot{text-align: center;padding-top:10pt;padding-bottom: 20pt;}" +
            "</style></head><body><div><div class=\"title\">%s</div><div class=\"from\">%s<span style=\"float: right\">%s</span></div>" +
            "<hr style=\"clear: both\"/><div class=\"introduce\">%s<div style=\"clear: both\"></div></div><div class=\"content\">%s</div>" +
            "<div class=\"clear foot\">--- The End ---</div></div><script>var as = document.getElementsByTagName(\"a\");" +
            "for(var i=0;i<as.length;i++){var a = as[i];if(a.getElementsByTagName('img').length>0)" +
            "{a.onclick=function(){return false;}}}; function openImage(obj){window.Interface.showImage(obj.src);return false;}" +
            "</script></body></html>";
    private Handler myHandler;
    private WebSettings settings;

    public NewsDetailProcesserImpl(ActionBarActivity mContext, TranslucentStatusHelper helper) {
        this.hascontent = false;
        this.mContext = mContext;
        this.mContext.setContentView(R.layout.activity_detail);
        this.myHandler = new Handler();
        this.helper = helper;
        initView();
        if (mContext.getIntent().getExtras().containsKey(NEWS_ITEM_KEY)) {
            mNewsItem = (NewsItem) mContext.getIntent().getSerializableExtra(NEWS_ITEM_KEY);
            mContext.setTitle("详情：" + mNewsItem.getTitle());
            NewsItem mNews = mNewsItem.getSN() == null ? FileCacheKit.getInstance().getAsObject(mNewsItem.getSid() + "", NewsItem.class) : mNewsItem;
            if (mNews == null) {
                makeRequest();
            } else {
                hascontent = true;
                mNewsItem = mNews;
                blindData(mNews);
            }
        } else {
            Toast.makeText(mContext, "缺少必要参数", Toast.LENGTH_SHORT).show();
            mContext.finish();
        }
    }

    private void blindData(NewsItem mNews) {
        String data = String.format(Locale.CHINA, webTemplate, mNews.getTitle(), mNews.getFrom(), mNews.getInputtime()
                , mNews.getHometext(), mNews.getContent());
        mWebView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
        mWebView.setVisibility(View.VISIBLE);
        mActionButtom.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActionButtom.setVisibility(View.VISIBLE);
                mActionButtom.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            }
        }, 200);
        mProgressBar.setVisibility(View.GONE);
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void initView() {
        this.loadFail = mContext.findViewById(R.id.loadFail);
        this.mWebView = (WebView) mContext.findViewById(R.id.webview);
        this.mProgressBar = (ProgressWheel) mContext.findViewById(R.id.loading);
        this.mActionButtom = (FloatingActionButton) mContext.findViewById(R.id.action);
        this.mActionButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentAction();
            }
        });
        mActionButtom.setScaleX(0);
        mActionButtom.setScaleY(0);
        settings = mWebView.getSettings();
        settings.setSupportZoom(false);
        settings.setAllowFileAccess(true);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (!PrefKit.getBoolean(mContext, mContext.getString(R.string.pref_hardware_accelerated_key), true)) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (NetKit.isWifiConnected()) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        settings.setTextZoom(PrefKit.getInt(mContext, "font_size", 100));
        mWebView.addJavascriptInterface(new JavaScriptInterface(mContext), "Interface");
        mWebView.setWebChromeClient(client);
        this.loadFail.setClickable(true);
        this.loadFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();
            }
        });
    }

    public void makeRequest() {

        NetKit.getInstance().getNewsBySid(mNewsItem.getSid() + "", new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                mProgressBar.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
                loadFail.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (!hascontent) {
                    loadFail.setVisibility(View.VISIBLE);
                } else {
                    blindData(mNewsItem);
                    mWebView.setVisibility(View.VISIBLE);
                }
                mProgressBar.setVisibility(View.GONE);
                Toolkit.showCrouton(mContext, R.string.message_no_network, Style.ALERT);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (Configure.STANDRA_PATTERN.matcher(responseString).find()) {
                    new AsyncTask<String, Integer, Boolean>() {

                        @Override
                        protected Boolean doInBackground(String... params) {
                            Document doc = Jsoup.parse(params[0]);
                            Elements newsHeadlines = doc.select(".body");
                            mNewsItem.setFrom(newsHeadlines.select(".where").html());
                            mNewsItem.setInputtime(newsHeadlines.select(".date").html());
                            mNewsItem.setHometext(newsHeadlines.select(".introduction").html());
                            Elements content = newsHeadlines.select(".content");
                            for (Element e : content.select("img")) {
                                e.attr("onclick", "openImage(this)");
                            }
                            mNewsItem.setContent(content.html());
                            Matcher snMatcher = Configure.SN_PATTERN.matcher(params[0]);
                            if (snMatcher.find())
                                mNewsItem.setSN(snMatcher.group(1));
                            hascontent = true;
                            FileCacheKit.getInstance().putAsync(mNewsItem.getSid() + "", Toolkit.getGson().toJson(mNewsItem), null);
                            return true;
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            if (result) {
                                blindData(mNewsItem);
                            }
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }.execute(responseString);
                } else {
                    onFailure(statusCode, headers, responseString, new RuntimeException());
                }
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
            }
        });

    }

    public Context getContext() {
        return mContext;
    }

    public void commentAction() {
        Intent intent = new Intent(mContext, NewsCommentActivity.class);
        intent.putExtra(NewsCommentProcesserImpl.SN_KEY, mNewsItem.getSN());
        intent.putExtra(NewsCommentProcesserImpl.SID_KEY, mNewsItem.getSid());
        intent.putExtra(NewsCommentProcesserImpl.TITLE_KEY, mNewsItem.getTitle());
        mContext.startActivity(intent);
    }

    public void shareAction() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_templates
                , mNewsItem.getTitle(), Configure.buildArticleUrl(mNewsItem.getSid() + "")));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.menu_share)));
    }

    public void viewInBrowser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(Configure.buildArticleUrl(mNewsItem.getSid() + ""));
        intent.setData(content_url);
        mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.choise_browser)));
    }

    public void handleFontSize() {
        FontSizeFragment fragment = FontSizeFragment.getInstance(settings.getTextZoom());
        fragment.show(mContext.getFragmentManager(), "Font Size");
        fragment.setSeekBarListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (fromUser) {
                    settings.setTextZoom(value);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&&client.myCallback!=null) {
            client.onHideCustomView();
            return true;
        }
        return false;
    }

    public void doBookmark() {
        if (hascontent) {
            String message;
            Style style;
            try {
                if (MyApplication.getInstance().getDbUtils().findById(NewsItem.class, mNewsItem.getSid()) == null) {
                    MyApplication.getInstance().getDbUtils().saveOrUpdate(mNewsItem);
                    message = "收藏成功";
                } else {
                    MyApplication.getInstance().getDbUtils().deleteById(NewsItem.class, mNewsItem.getSid());
                    message = "取消收藏成功";
                }
                style = Style.INFO;
            } catch (DbException e) {
                message = "操作失败";
                style = Style.ALERT;
            }
            Toolkit.showCrouton(mContext, message, style);
        }
    }

    private class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showImage(final String imageSrc) {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(mContext, ImageViewActivity.class);
                    intent.putExtra(ImageViewActivity.IMAGE_URL, imageSrc);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        mWebView.destroy();
    }

    @Override
    public void onResume() {
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        mWebView.onPause();
    }

    private class VideoWebChromeClient extends WebChromeClient {
        private View myView = null;
        CustomViewCallback myCallback = null;
        private int orientation;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (myCallback != null) {
                myCallback.onCustomViewHidden();
                myCallback = null;
                return;
            }
            orientation = mContext.getRequestedOrientation();
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mContext.getSupportActionBar().hide();
            helper.getOption().setWithActionBar(false);
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            mWebView.setVisibility(View.GONE);
            mActionButtom.setVisibility(View.GONE);
            parent.addView(view);
            myView = view;
            myCallback = callback;
        }

        @Override
        public void onHideCustomView() {

            if (myView != null) {

                if (myCallback != null) {
                    myCallback.onCustomViewHidden();
                    myCallback = null;
                }
                mContext.setRequestedOrientation(orientation);
                mContext.getSupportActionBar().show();
                helper.getOption().setWithActionBar(true);
                ViewGroup parent = (ViewGroup) myView.getParent();
                parent.removeView(myView);
                mWebView.setVisibility(View.VISIBLE);
                mActionButtom.setVisibility(View.VISIBLE);

                myView = null;
            }
        }
    }
}
