package com.ywwxhz.processers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.ywwxhz.MyApplication;
import com.ywwxhz.activitys.ImageViewActivity;
import com.ywwxhz.activitys.NewsCommentActivity;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.DataProviderCallback;
import com.ywwxhz.data.impl.NewsDetailProvider;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.fragments.FontSizeFragment;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.database.exception.DbException;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.Toolkit;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * cnBetaReader
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:48.
 */
public class NewsDetailProcesserImpl extends BaseProcesserImpl implements DataProviderCallback<String> {
    public static final String NEWS_ITEM_KEY = "key_news_item";
    private View loadFail;
    private WebView mWebView;
    private ActionBarActivity mContext;
    private boolean hascontent;
    private NewsItem mNewsItem;
    private ProgressWheel mProgressBar;
    private FloatingActionButton mActionButtom;
    private VideoWebChromeClient client = new VideoWebChromeClient();
    private NewsDetailProvider provider;

    private String webTemplate = "<!DOCTYPE html><html><head><title></title><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"/>" +
            "<style>body{word-break: break-all;font-size: 11pt;}" +
            ".title{font-size: 18pt;color: #1473af;}" +
            ".from{font-size: 10pt;padding-top: 4pt;}" +
            ".introduce{clear: both;background-color:#F1F1F1;color: #444;padding: 13pt 5pt 8pt 5pt;margin-top: 5pt;quotes: \"\\201C\"\"\\201D\"\"\\2018\"\"\\2019\";}" +
            ".introduce img{padding:0;width:0;height:0}" +
            ".introduce p:before {color:#ccc;content:open-quote;font-size:4em;line-height:.1em;margin-right:.25em;vertical-align:-.4em;}"+
            ".introduce p{margin:0;line-height: 16pt}" +
            ".introduce div{margin: 0px !important;}" +
            ".content{padding-top:10pt;}" +
            ".content p {text-indent: 2em;line-height: 16pt;}"+
            ".content p iframe{display: block;width: 100%% !important}" +
            "ol, ul, li {list-style: none;margin: 0;padding: 0;vertical-align: baseline;}"+
            ".content table, .content td{border: 1px solid #000;border-collapse: collapse;border-spacing: 0;}"+
            ".content table p {text-indent: 0;}"+
            ".content video{display: block;width:100%% !important;height:auto !important}" +
            ".content img{display: block;max-width: 100%% !important;height: auto; !important;margin: 0 auto}a{text-decoration: none;color:#2f7cad;}" +
            ".content blockquote {margin: 0; background: url(\"file:///android_asset/left_quote.jpg\") no-repeat scroll 1%% 4pt #F1F1F1; color: #878787;padding: 1pt 2pt 1pt 10pt;}"+
            ".content embed{display: block;width: 100%% !important;}" +
            ".clear{clear: both;}.foot{text-align: center;padding-top:10pt;padding-bottom: 20pt;}" +
            "</style></head><body><div><div class=\"title\">%s</div><div class=\"from\">%s<span style=\"float: right\">%s</span></div>" +
            "<div class=\"introduce\">%s<div style=\"clear: both\"></div></div><div class=\"content\">%s</div>" +
            "<div class=\"clear foot\">--- The End ---</div></div><script>var as = document.getElementsByTagName(\"a\");" +
            "for(var i=0;i<as.length;i++){var a = as[i];if(a.getElementsByTagName('img').length>0)" +
            "{a.onclick=function(){return false;}}}; function openImage(obj){window.Interface.showImage(obj.src);return false;}" +
            "var iframes = document.getElementsByTagName('iframe');for(var i=0;i<iframes.length;i++){var iframe = iframes[i];iframe.style.height = iframe.offsetWidth *3/4+\"px\"}var embeds = document.getElementsByTagName('embed');for(var i=0;i<embeds.length;i++){var embed = embeds[i];embed.style.height = embed.offsetWidth *3/4+\"px\"}var videos = document.getElementsByTagName('video');for(var i=0;i<videos.length;i++){var video = videos[i];video.style.height = video.offsetWidth *3/4+\"px\"}"+
            "</script></body></html>";
    private Handler myHandler;
    private WebSettings settings;

    public NewsDetailProcesserImpl(ActionBarActivity mContext) {
        this.hascontent = false;
        this.mContext = mContext;
        this.mContext.setContentView(R.layout.activity_detail);
        this.provider = new NewsDetailProvider(mContext);
        this.provider.setCallback(this);
        this.myHandler = new Handler();
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
        provider.loadNewsAsync(mNewsItem.getSid()+"");
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

    @Override
    public void onLoadStart() {
        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
        loadFail.setVisibility(View.GONE);
    }

    @Override
    public void onLoadSuccess(String resp) {
        if (Configure.STANDRA_PATTERN.matcher(resp).find()) {
            new AsyncTask<String,String,Boolean>(){
                @Override
                protected Boolean doInBackground(String... strings) {
                    hascontent = NewsDetailProvider.handleResponceString(mNewsItem,strings[0]);
                    return hascontent;
                }

                @Override
                protected void onPostExecute(Boolean hascontent) {
                    if(hascontent){
                        blindData(mNewsItem);
                        mProgressBar.setVisibility(View.GONE);
                    }else{
                        onLoadFailure();
                    }
                }
            }.execute(resp);
        } else {
            onLoadFailure();
        }
    }

    @Override
    public void onLoadFinish() {

    }

    @Override
    public void onLoadFailure() {
        if (!hascontent) {
            loadFail.setVisibility(View.VISIBLE);
        } else {
            blindData(mNewsItem);
            mWebView.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.GONE);
        Toolkit.showCrouton(mContext, R.string.message_no_network, Style.ALERT);
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
        private int requiredOrientation;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (myCallback != null) {
                myCallback.onCustomViewHidden();
                myCallback = null;
                return;
            }
            requiredOrientation = mContext.getRequestedOrientation();
            orientation = mContext.getResources().getConfiguration().orientation;
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mContext.getSupportActionBar().hide();
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            mWebView.setVisibility(View.GONE);
            mActionButtom.setVisibility(View.GONE);
            parent.addView(view);
            view.setBackgroundColor(Color.BLACK);
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
                mContext.setRequestedOrientation(requiredOrientation);
                mContext.getSupportActionBar().show();
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                parent.removeView(myView);
                mWebView.setVisibility(View.VISIBLE);
                mActionButtom.setVisibility(View.VISIBLE);

                myView = null;
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mContext.finish();
                break;
            case R.id.menu_share:
                shareAction();
                break;
            case R.id.menu_view_in_browser:
                viewInBrowser();
                break;
            case R.id.menu_reflush:
                makeRequest();
                break;
            case R.id.menu_font_size:
                handleFontSize();
                break;
            case R.id.menu_book_mark:
                doBookmark();
                break;
        }
        return false;
    }
}
