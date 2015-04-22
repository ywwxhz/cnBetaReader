package com.ywwxhz.processers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.ywwxhz.MyApplication;
import com.ywwxhz.activitys.ImageViewActivity;
import com.ywwxhz.activitys.NewsCommentActivity;
import com.ywwxhz.activitys.NewsDetailActivity;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.DataProviderCallback;
import com.ywwxhz.data.impl.NewsDetailProvider;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.fragments.FontSizeFragment;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.CroutonStyle;
import com.ywwxhz.lib.ThemeManger;
import com.ywwxhz.lib.database.exception.DbException;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.Toolkit;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * cnBetaReader
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:48.
 */
public class NewsDetailProcesser extends BaseProcesserImpl<String, NewsDetailProvider> implements DataProviderCallback<String> {
    private View loadFail;
    private WebView mWebView;
    private boolean hascontent;
    private NewsItem mNewsItem;
    private ProgressWheel mProgressBar;
    private FloatingActionButton mActionButtom;
    private VideoWebChromeClient client = new VideoWebChromeClient();
    private boolean showImage;

    private String webTemplate = "<!DOCTYPE html><html><head><title></title><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"/>" +
            "<style>body{word-break: break-all;font-size: 11pt;}" +
            "#title{font-size: 18pt;color: #%s;}" +
            ".from{font-size: 10pt;padding-top: 4pt;}" +
            "#introduce{clear: both;padding: 13pt 5pt 8pt 5pt;margin-top: 5pt;quotes: \"\\201C\"\"\\201D\"\"\\2018\"\"\\2019\";}" +
            "#introduce img{padding:0;width:0;height:0}%s" +
            "#introduce:before {color: #CCC;content: open-quote;font-size: 4em;line-height: 0.2em;margin-right: .2em;float: left;margin-top: .17em;}" +
            "#introduce p{margin:0;line-height: 16pt}" +
            "#introduce div{margin: 0px !important;}" +
            ".content{padding-top:10pt;}" +
            ".content p {text-indent: 2em;line-height: 16pt;}" +
            "ol, ul, li {list-style: none;margin: 0;padding: 0;vertical-align: baseline;}" +
            ".content table, .content td{border: 1px solid #000;border-collapse: collapse;border-spacing: 0;}" +
            ".content table p {text-indent: 0;}" +
            ".content iframe{display: block;max-width: 100%% ;margin: auto;border: 0;}" +
            ".content video{display: block; max-width:100%%; margin: auto;border: 0;}" +
            ".content embed{display: block; max-width: 100%%;}" +
            ".content img{display: block !important;max-width: 100%% !important;height: auto !important;margin: 0 auto}a{text-decoration: none;color:#2f7cad;}" +
            ".content blockquote {margin: 0; background: url(\"file:///android_asset/left_quote.jpg\") no-repeat scroll 1%% 4pt #F1F1F1; color: #878787;padding: 1pt 2pt 1pt 10pt;}" +
            ".clear{clear: both;}.foot{text-align: center;padding-top:10pt;padding-bottom: 20pt;}" +
            "</style></head>" +
            "<body><div><div id=\"title\">%s</div><div class=\"from\">%s<span style=\"float: right\">%s</span></div>" +
            "<div id=\"introduce\">%s<div style=\"clear: both\"></div></div><div class=\"content\">%s</div>" +
            "<div class=\"clear foot\">--- The End ---</div></div>" +
            "<script>" +
            "var enableImage=%s;var image=\"data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzAwIiB2aWV3Qm94PSIwIDAgNjAwIDMwMCIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzAwIiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMTc3LjY1NjI1IiB5PSIxNjMuMiIgc3R5bGU9ImZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZTozMHB0Ij7ngrnlh7vliqDovb3lm77niYc8L3RleHQ+PC9nPjwvc3ZnPg==\";var error=\"data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzAwIiB2aWV3Qm94PSIwIDAgNjAwIDMwMCIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzAwIiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMjE4LjQzNzUiIHk9IjE2My4yIiBzdHlsZT0iZmlsbDojQUFBQUFBO2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjMwcHQiPueCueWHu+mHjeivlTwvdGV4dD48L2c+PC9zdmc+\";var loading=\"data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzAwIiB2aWV3Qm94PSIwIDAgNjAwIDMwMCIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzAwIiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMjM4LjgyODEyNSIgeT0iMTYzLjIiIHN0eWxlPSJmaWxsOiNBQUFBQUE7Zm9udC13ZWlnaHQ6Ym9sZDtmb250LWZhbWlseTpBcmlhbCwgSGVsdmV0aWNhLCBPcGVuIFNhbnMsIHNhbnMtc2VyaWYsIG1vbm9zcGFjZTtmb250LXNpemU6MzBwdCI+5Yqg6L295LitPC90ZXh0PjwvZz48L3N2Zz4=\";var flash=\"data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzAwIiB2aWV3Qm94PSIwIDAgNjAwIDMwMCIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzAwIiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMTg3Ljc1IiB5PSIxNjMuMiIgc3R5bGU9ImZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZTozMHB0Ij5GbGFzaCBWaWRlbzwvdGV4dD48L2c+PC9zdmc+\";(function(){var l=document.querySelectorAll(\".content img\");imageSrcs=[];for(var j=0;j<l.length;j++){var k=l[j];imageSrcs[j]=k.src;k.setAttribute(\"dest-src\",k.src);k.setAttribute(\"pos\",j);if(enableImage){loadImage(k)}else{k.removeAttribute(\"src\");k.setAttribute(\"src\",image);k.onclick=function(){loadImage(this)}}}var b=document.querySelectorAll(\".content a>img\");for(var j=0;j<b.length;j++){var k=b[j];var o=k.parentNode;var e=o.parentNode;e.replaceChild(k,o)}var m=document.querySelectorAll(\"iframe\");for(var j=0;j<m.length;j++){var g=m[j];g.width=g.offsetWidth;g.height=g.offsetWidth*3/4}var f=document.querySelectorAll(\"video\");for(var j=0;j<f.length;j++){var d=f[j];d.width=d.offsetWidth;d.height=d.offsetWidth*3/4}var c=document.querySelectorAll(\"embed\");for(var j=0;j<c.length;j++){var n=c[j];if(n.type==\"application/x-shockwave-flash\"){var h=new Image();h.setAttribute(\"src\",flash);h.setAttribute(\"id\",\"video_\"+j);h.setAttribute(\"video-src\",n.src);h.onclick=function(){q(this)};n.parentNode.replaceChild(h,n);console.log(\"flash video \")}else{console.log(\"other \");n.style.height=n.offsetWidth*3/4+\"px\"}}function q(v){console.log(\"video id \"+v.id+\"  loading\");var i=v.getAttribute(\"video-src\");var r=i.match(/.*sohu.*id=(\\d+).*/);if(r){var p=\"http://api.tv.sohu.com/v4/video/info/\"+r[1]+\".json?site=2&api_key=9854b2afa779e1a6bff1962447a09dbd\";console.log(\"load sohu video id \"+r[1]);window.Interface.loadSohuVideo(v.id,p);return}var t=i.match(/.*tudou.*\\/v\\/(\\S+)?\\/&.*/);if(t){var s=document.createElement(\"iframe\");s.src=\"http://www.tudou.com/programs/view/html5embed.action?code=\"+t[1];s.width=v.offsetWidth+\"\";s.height=v.offsetWidth*3/4+\"\";s.onload=function(){console.log(\"tudou html5 video player loading success\")};v.parentNode.replaceChild(s,v);return}var u=i.match(/.*youku.*\\/sid\\/(\\S+)?\\/v.*/);if(u){var s=document.createElement(\"iframe\");s.src=\"http://player.youku.com/embed/\"+u[1];s.width=v.offsetWidth+\"\";s.height=v.offsetWidth*3/4+\"\";s.onload=function(){console.log(\"youku html5 video player loading success\")};v.parentNode.replaceChild(s,v);return}var a=i.match(/.*qq.*vid=(\\S+)?&?/);if(a){var s=document.createElement(\"iframe\");s.src=\"http://v.qq.com/iframe/player.html?vid=\"+a[1];+\"&amp;width=\"+v.offsetWidth+\"&amp;height=\"+v.offsetWidth*3/4+\"&amp;auto=0\";s.width=v.offsetWidth+\"\";s.height=v.offsetWidth*3/4+\"\";s.onload=function(){console.log(\"QQ html5 video player loading success\")};v.parentNode.replaceChild(s,v);return}console.log(i)}})();function VideoCallBack(c,b){var d=document.getElementById(c);if(d){var a=document.createElement(\"video\");a.src=b;a.width=d.offsetWidth+\"\";a.height=d.offsetWidth*3/4+\"\";a.controls=\"controls\";d.parentNode.replaceChild(a,d)}else{console.log(\"Illegal viewid\")}}function showAllImage(){var b=document.querySelectorAll(\".content img\");for(var a=0;a<b.length;a++){loadImage(b[a])}}function loadImage(a){var b=new Image();a.setAttribute(\"src\",loading);b.src=a.getAttribute(\"dest-src\");b.onload=function(){a.setAttribute(\"src\",a.getAttribute(\"dest-src\"));a.onclick=function(){openImage(this)}};b.onerror=function(){a.setAttribute(\"src\",error);a.onclick=function(){loadImage(this)}}}function openImage(a){console.log(a.getAttribute(\"pos\"));window.Interface.showImage(a.getAttribute(\"pos\"),imageSrcs);return false}function setNight(a){if(a){document.body.style.backgroundColor=\"#202733\";document.body.style.color=\"#9bafcb\";document.getElementById(\"introduce\").style.backgroundColor=\"#262f3d\";document.getElementById(\"introduce\").style.color=\"#616d80\"}else{document.body.style.backgroundColor=\"#FFF\";document.body.style.color=\"#000\";document.getElementById(\"introduce\").style.backgroundColor=\"#F1F1F1\";document.getElementById(\"introduce\").style.color=\"#444\"}};" +
            "</script></body></html>";
    private String night = "body{color:#9bafcb}#introduce{background-color:#262f3d;color:#616d80}";
    private String light = "#introduce{background-color:#F1F1F1;color: #444;}";
    private Handler myHandler;
    private WebSettings settings;

    public NewsDetailProcesser(NewsDetailProvider provider) {
        super(provider);
    }

    private void blindData(NewsItem mNews) {
        String colorString = Integer.toHexString(titleColor);
        String add;
        if (ThemeManger.isNightTheme(getActivity())) {
            add = night;
        } else {
            add = light;
        }
        String data = String.format(Locale.CHINA, webTemplate, colorString.substring(2, colorString.length()), add, mNews.getTitle(), mNews.getFrom(), mNews.getInputtime()
                , mNews.getHometext(), mNews.getContent(), showImage);
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
        this.loadFail = mActivity.findViewById(R.id.message);
        this.mWebView = (WebView) mActivity.findViewById(R.id.webview);
        if (ThemeManger.isNightTheme(getActivity())) {
            this.mWebView.setBackgroundColor(windowBackground);
        }
        this.mProgressBar = (ProgressWheel) mActivity.findViewById(R.id.loading);
        this.mActionButtom = (FloatingActionButton) mActivity.findViewById(R.id.action);
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
        if (!PrefKit.getBoolean(mActivity, mActivity.getString(R.string.pref_hardware_accelerated_key), true)) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (NetKit.isWifiConnected()) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        settings.setTextZoom(PrefKit.getInt(mActivity, "font_size", 100));
        mWebView.addJavascriptInterface(new JavaScriptInterface(mActivity), "Interface");
        mWebView.setWebChromeClient(client);
        mWebView.setWebViewClient(new MyWebViewClient());
        this.loadFail.setClickable(true);
        this.loadFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();
            }
        });
    }

    public void makeRequest() {
        provider.loadNewsAsync(mNewsItem.getSid() + "");
    }

    public void commentAction() {
        Intent intent = new Intent(mActivity, NewsCommentActivity.class);
        intent.putExtra(NewsCommentActivity.SN_KEY, mNewsItem.getSN());
        intent.putExtra(NewsCommentActivity.SID_KEY, mNewsItem.getSid());
        intent.putExtra(NewsCommentActivity.TITLE_KEY, mNewsItem.getTitle());
        mActivity.startActivity(intent);
    }

    public void shareAction() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mActivity.getString(R.string.share_templates
                , mNewsItem.getTitle(), Configure.buildArticleUrl(mNewsItem.getSid() + "")));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(Intent.createChooser(intent, mActivity.getString(R.string.menu_share)));
    }

    public void viewInBrowser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(Configure.buildArticleUrl(mNewsItem.getSid() + ""));
        intent.setData(content_url);
        mActivity.startActivity(Intent.createChooser(intent, mActivity.getString(R.string.choise_browser)));
    }

    public void handleFontSize() {
        FontSizeFragment fragment = FontSizeFragment.getInstance(settings.getTextZoom());
        fragment.show(mActivity.getFragmentManager(), "Font Size");
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
        if (keyCode == KeyEvent.KEYCODE_BACK && client.myCallback != null) {
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
                style = CroutonStyle.INFO;
            } catch (DbException e) {
                message = "操作失败";
                style = Style.ALERT;
            }
            Toolkit.showCrouton(mActivity, message, style);
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
            new AsyncTask<String, String, Boolean>() {
                @Override
                protected Boolean doInBackground(String... strings) {
                    hascontent = NewsDetailProvider.handleResponceString(mNewsItem, strings[0]);
                    return hascontent;
                }

                @Override
                protected void onPostExecute(Boolean hascontent) {
                    if (hascontent) {
                        blindData(mNewsItem);
                        mProgressBar.setVisibility(View.GONE);
                    } else {
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
        Toolkit.showCrouton(mActivity, R.string.message_no_network, Style.ALERT);
    }

    private class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showImage(String pos, final String[] imageSrcs) {
            final int posi;
            try{
                posi = Integer.parseInt(pos);
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mContext, ImageViewActivity.class);
                        intent.putExtra(ImageViewActivity.IMAGE_URLS, imageSrcs);
                        intent.putExtra(ImageViewActivity.CURRENT_POS,posi);
                        mContext.startActivity(intent);
                    }
                });
            }catch (Exception e){
                Log.d(getClass().getName(), "Illegal argument");
            }
        }

        @JavascriptInterface
        public void loadSohuVideo(final String hoder_id, final String requestUrl) {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        NetKit.getInstance().getClient().get(requestUrl,new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    mWebView.loadUrl("javascript:VideoCallBack(\""+hoder_id+"\",\""+response.getJSONObject("data").getString("url_high_mp4")+"\")");
                                }catch (Exception e){
                                    Toolkit.showCrouton(mActivity,"搜狐视频加载失败",Style.ALERT);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toolkit.showCrouton(mActivity,"搜狐视频加载失败",Style.ALERT);
                            }
                        });
                    }
                });
        }

    }

    @Override
    public void onDestroy() {
        mWebView.destroy();
    }

    @Override
    public void assumeView(View view) {
        this.hascontent = false;
        this.myHandler = new Handler();
        initView();
        showImage = PrefKit.getBoolean(mActivity, R.string.pref_show_detail_image_key, true);
        mNewsItem = (NewsItem) mActivity.getIntent().getSerializableExtra(NewsDetailActivity.NEWS_ITEM_KEY);
        mActivity.setTitle("详情：" + mNewsItem.getTitle());
    }

    @Override
    public void loadData(boolean startup) {
        NewsItem mNews = mNewsItem.getSN() == null ? FileCacheKit.getInstance().getAsObject(mNewsItem.getSid() + "", NewsItem.class) : mNewsItem;
        if (mNews == null) {
            makeRequest();
        } else {
            hascontent = true;
            mNewsItem = mNews;
            blindData(mNews);
        }
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
            requiredOrientation = mActivity.getRequestedOrientation();
            orientation = mActivity.getResources().getConfiguration().orientation;
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mActivity.getSupportActionBar().hide();
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) parent.getLayoutParams();
            params.topMargin = 0;
            parent.setLayoutParams(params);
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
                mActivity.setRequestedOrientation(orientation);
                mActivity.setRequestedOrientation(requiredOrientation);
                mActivity.getSupportActionBar().show();
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) parent.getLayoutParams();
                params.topMargin = mActivity.getSupportActionBar().getHeight();
                parent.setLayoutParams(params);
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
                mActivity.finish();
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
            case 0:
                showAllImage();
                break;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!showImage) {
            menu.add(0, 0, 0, "显示全部图片");
        }
    }

    private void showAllImage() {
        mWebView.loadUrl("javascript:showAllImage()");
    }

    private void nightMode() {
        mWebView.loadUrl("javascript:setNight(true)");
    }

    class MyWebViewClient extends WebViewClient{
        private static final String TAG = "WebView ImageLoader";
        private boolean finish = false;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            finish = false;
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            mActivity.startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            System.out.println("MyWebViewClient.onPageFinished");
            super.onPageFinished(view, url);
            finish = true;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            System.out.println("MyWebViewClient.shouldInterceptRequest(view,url) url = [" + url + "]");
            String prefix = MimeTypeMap.getFileExtensionFromUrl(url);
            if(!TextUtils.isEmpty(prefix)) {
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(prefix);
                if(mimeType!=null&&mimeType.startsWith("image")) {
                    if(finish||showImage) {
                        File image = ImageLoader.getInstance().getDiskCache().get(url);
                        if (image != null) {
                            System.out.println("load Image From disk cache");
                            try {
                                return new WebResourceResponse(mimeType, "UTF-8", new FileInputStream(image));
                            } catch (FileNotFoundException ignored) {
                            }
                        } else {
                            System.out.println("load Image From net");
                        }
                    }else{
                        System.out.println("Load Image Hoder");
                        try {
                            return new WebResourceResponse("image/svg+xml", "UTF-8", mActivity.getAssets().open("image.svg"));
                        } catch (IOException ignored) {
                        }
                    }
                }else{
                    System.out.println("load other resourse");
                }
            }
            return super.shouldInterceptRequest(view,url);
        }
    }
}
