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
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.DataProviderCallback;
import com.ywwxhz.data.impl.NewsDetailProvider;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.fragments.FontSizeFragment;
import com.ywwxhz.fragments.NewsDetailFragment;
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
    private boolean convertFlashToHtml5;

    public void setCallBack(NewsDetailFragment.NewsDetailCallBack callBack) {
        this.callBack = callBack;
    }

    private NewsDetailFragment.NewsDetailCallBack callBack;

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
            ".content table p {text-indent: 0;} .content object{display: block;max-width: 100%% !important;height: auto !important;}" +
            ".content iframe{display: block;max-width: 100%% ;margin: auto;border: 0;}" +
            ".content video{display: block; max-width:100%%; margin: auto;border: 0;background-color: #000;}" +
            ".content embed{display: block; max-width: 100%%;}" +
            ".content img{display: block !important;max-width: 100%% !important;height: auto !important;margin: 0 auto}a{text-decoration: none;color:#2f7cad;}" +
            //".content blockquote {margin: 0; background: url(\"file:///android_asset/left_quote.jpg\") no-repeat scroll 1%% 4pt #F1F1F1; color: #878787;padding: 1pt 2pt 1pt 10pt;}" +
            ".content blockquote{background-color:#F1F1F1;color: #444;padding: 13pt 5pt 8pt 5pt;margin: 0;quotes: \"\\201C\"\"\\201D\"\"\\2018\"\"\\2019\";}" +
            ".content blockquote:before {color: #CCC;content: open-quote;font-size: 4em;line-height: .01em;margin-left: .1em;vertical-align: -.4em;}" +
            ".content blockquote p{}"+
            ".clear{clear: both;}.foot{text-align: center;padding-top:10pt;padding-bottom: 20pt;}" +
            "</style></head>" +
            "<body><div><div id=\"title\">%s</div><div class=\"from\">%s<span style=\"float: right\">%s</span></div>" +
            "<div id=\"introduce\">%s<div style=\"clear: both\"></div></div><div class=\"content\">%s</div>" +
            "<div class=\"clear foot\">--- The End ---</div></div>" +
            "<script>" +
            "var enableImage=%s;var enableFlashToHtml5=%s;var image=\"data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMTc3LjY1NjI1IiB5PSIyMDAuNyIgc3R5bGU9ImZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZTozMHB0Ij7ngrnlh7vliqDovb3lm77niYc8L3RleHQ+PC9nPjwvc3ZnPg==\";var error=\"data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMjE4LjQzNzUiIHk9IjIwMC43IiBzdHlsZT0iZmlsbDojQUFBQUFBO2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjMwcHQiPueCueWHu+mHjeivlTwvdGV4dD48L2c+PC9zdmc+\";var loading=\"data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMjM4LjgyODEyNSIgeT0iMjAwLjciIHN0eWxlPSJmaWxsOiNBQUFBQUE7Zm9udC13ZWlnaHQ6Ym9sZDtmb250LWZhbWlseTpBcmlhbCwgSGVsdmV0aWNhLCBPcGVuIFNhbnMsIHNhbnMtc2VyaWYsIG1vbm9zcGFjZTtmb250LXNpemU6MzBwdCI+5Yqg6L295LitPC90ZXh0PjwvZz48L3N2Zz4=\";var flash=\"data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMTg3Ljc1IiB5PSIyMDAuNyIgc3R5bGU9ImZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZTozMHB0Ij5GbGFzaCBWaWRlbzwvdGV4dD48L2c+PC9zdmc+\";var video_img=\"data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iNjAwIiBoZWlnaHQ9IjM3NSIgdmlld0JveD0iMCAwIDYwMCAzNzUiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzYwMHgzNzUvYXV0by90ZXh0OlZpZGVvCkNyZWF0ZWQgd2l0aCBIb2xkZXIuanMgMi41LjIuCkxlYXJuIG1vcmUgYXQgaHR0cDovL2hvbGRlcmpzLmNvbQooYykgMjAxMi0yMDE1IEl2YW4gTWFsb3BpbnNreSAtIGh0dHA6Ly9pbXNreS5jbwotLT48ZGVmcy8+PHJlY3Qgd2lkdGg9IjYwMCIgaGVpZ2h0PSIzNzUiIGZpbGw9IiNFRUVFRUUiLz48Zz48dGV4dCB4PSIyNDUuNTQ2ODc1IiB5PSIyMDAuNyIgc3R5bGU9ImZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZTozMHB0Ij5WaWRlbzwvdGV4dD48L2c+PC9zdmc+\";var no_support=\"data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMTM2Ljg3NSIgeT0iMjAwLjciIHN0eWxlPSJmaWxsOiNBQUFBQUE7Zm9udC13ZWlnaHQ6Ym9sZDtmb250LWZhbWlseTpBcmlhbCwgSGVsdmV0aWNhLCBPcGVuIFNhbnMsIHNhbnMtc2VyaWYsIG1vbm9zcGFjZTtmb250LXNpemU6MzBwdCI+5bCa5pyq5pSv5oyB6K+l6KeG6aKR5rqQPC90ZXh0PjwvZz48L3N2Zz4=\";(function(){var b=document.querySelectorAll(\".content a>img\");for(var j=0;j<b.length;j++){var k=b[j];var n=k.parentNode;var e=n.parentNode;e.replaceChild(k,n)}imgs=document.querySelectorAll(\".content img\");imageSrcs=[];for(var j=0;j<imgs.length;j++){var k=imgs[j];imageSrcs[j]=k.src;k.setAttribute(\"dest-src\",k.src);k.setAttribute(\"pos\",j);if(enableImage){loadImage(k)}else{k.removeAttribute(\"src\");k.setAttribute(\"src\",image);k.onclick=function(){loadImage(this)}}}var l=document.querySelectorAll(\"iframe\");for(var j=0;j<l.length;j++){var g=l[j];fixWidthAndHight(g,d)}var f=document.querySelectorAll(\"video\");for(var j=0;j<f.length;j++){var d=f[j];fixWidthAndHight(d,d);d.poster=video_img}var c=document.querySelectorAll(\"embed\");for(var j=0;j<c.length;j++){var m=c[j];if(m.type==\"application/x-shockwave-flash\"){var h=new Image();h.setAttribute(\"src\",flash);h.setAttribute(\"id\",\"video_\"+j);h.setAttribute(\"video-src\",m.src);h.setAttribute(\"video-params\",m.flashvars); if(enableFlashToHtml5)h.onclick=function(){o(this)};m.parentNode.replaceChild(h,m)}else{m.height=m.offsetWidth*10/16}}function o(u){u.setAttribute(\"src\",loading);var i=u.getAttribute(\"video-src\");var q=i.match(/.*sohu.*id=(\\d+).*/);if(q){var p=\"http://api.tv.sohu.com/v4/video/info/\"+q[1]+\".json?site=2&api_key=9854b2afa779e1a6bff1962447a09dbd\";window.Interface.loadSohuVideo(u.id,p);return}var s=i.match(/.*tudou.*\\/v\\/(\\S+)?\\/&.*/);if(s){var r=document.createElement(\"iframe\");r.src=\"http://www.tudou.com/programs/view/html5embed.action?code=\"+s[1];fixWidthAndHight(r,u);u.parentNode.replaceChild(r,u);return}var t=i.match(/.*youku.*\\/sid\\/(\\S+)?\\/v.*/);if(t){var r=document.createElement(\"iframe\");r.src=\"http://player.youku.com/embed/\"+t[1];fixWidthAndHight(r,u);u.parentNode.replaceChild(r,u);return}var a=i.match(/.*qq.*vid=(\\S+)?&?/);if(a){var r=document.createElement(\"iframe\");r.src=\"http://v.qq.com/iframe/player.html?vid=\"+a[1];+\"&amp;width=\"+u.offsetWidth+\"&amp;height=\"+u.offsetWidth*10/16+\"&amp;auto=0\";fixWidthAndHight(r,u);u.parentNode.replaceChild(r,u);return}u.setAttribute(\"src\",no_support);window.Interface.showMessage(\"尚未支持 \"+getUrlDomain(i)+\" 视频源\",\"info\")}})();function VideoCallBack(c,b,e){var d=document.getElementById(c);if(d){var a=document.createElement(\"video\");a.src=b;a.poster=e;fixWidthAndHight(a,d);a.controls=\"controls\";d.parentNode.replaceChild(a,d)}else{console.log(\"Illagel viewid\")}}function fixWidthAndHight(b,a){b.width=a.offsetWidth+\"\";b.height=a.offsetWidth*10/16+\"\"}function showAllImage(){for(var a=0;a<imgs.length;a++){loadImage(imgs[a])}}function loadImage(a){var b=new Image();a.setAttribute(\"src\",loading);b.src=a.getAttribute(\"dest-src\");b.onload=function(){a.setAttribute(\"src\",a.getAttribute(\"dest-src\"));a.onclick=function(){openImage(this)}};b.onerror=function(){a.setAttribute(\"src\",error);a.onclick=function(){loadImage(this)}}}function openImage(a){console.log(a.getAttribute(\"pos\"));window.Interface.showImage(a.getAttribute(\"pos\"),imageSrcs);return false}function setNight(a){if(a){document.body.style.backgroundColor=\"#202733\";document.body.style.color=\"#9bafcb\";document.getElementById(\"introduce\").style.backgroundColor=\"#262f3d\";document.getElementById(\"introduce\").style.color=\"#616d80\"}else{document.body.style.backgroundColor=\"#FFF\";document.body.style.color=\"#000\";document.getElementById(\"introduce\").style.backgroundColor=\"#F1F1F1\";document.getElementById(\"introduce\").style.color=\"#444\"}};function getUrlDomain(url){var tmp = url.split('/');if(tmp!=null && tmp[0].indexOf('http')!=-1){return tmp[2];}else{return \"unknow\";}}" +
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
                , mNews.getHometext(), mNews.getContent(), showImage,convertFlashToHtml5);
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
        if(callBack!=null){
            callBack.onNewsLoadFinish(mNewsItem,true);
        }
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})

    private void initView(View view) {
        this.loadFail = view.findViewById(R.id.message);
        this.mWebView = (WebView) view.findViewById(R.id.webview);
        if (ThemeManger.isNightTheme(getActivity())) {
            this.mWebView.setBackgroundColor(windowBackground);
        }
        this.mProgressBar = (ProgressWheel) view.findViewById(R.id.loading);
        this.mActionButtom = (FloatingActionButton) view.findViewById(R.id.action);
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
        if(callBack!=null){
            callBack.CommentAction(mNewsItem.getSid(),mNewsItem.getSN(),mNewsItem.getTitle());
        }
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
        System.out.println("NewsDetailProcesser.onKeyDown");
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
            if(callBack!=null){
                callBack.onNewsLoadFinish(mNewsItem,false);
            }
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
                                    mWebView.loadUrl("javascript:VideoCallBack(\""+hoder_id+"\",\""+response.getJSONObject("data").getString("url_high_mp4")+"\",\""+response.getJSONObject("data").getString("hor_big_pic")+"\")");
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
        @JavascriptInterface
        public void showMessage(final String message,final String type) {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toolkit.showCrouton(mActivity,message,CroutonStyle.getStyle(type));
                }
            });
        }

    }

    @Override
    public void onDestroy() {
        mWebView.stopLoading();
        mWebView.destroy();
    }

    @Override
    public void assumeView(View view) {
        this.hascontent = false;
        this.myHandler = new Handler();
        initView(view);
        showImage = PrefKit.getBoolean(mActivity, R.string.pref_show_detail_image_key, true);
        convertFlashToHtml5 = PrefKit.getBoolean(mActivity,R.string.pref_flash_to_html5_key,true);
    }

    public void setNewsItem(NewsItem newsItem) {
        this.mNewsItem = newsItem;
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
        mWebView.resumeTimers();
    }

    @Override
    public void onPause() {
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    private class VideoWebChromeClient extends WebChromeClient {
        private View myView = null;
        CustomViewCallback myCallback = null;
        private int orientation;
        private int requiredOrientation;

        @Override
        public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
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
            mWebView.setVisibility(View.GONE);
            if(callBack!=null){
                callBack.onShowVideo(true);
            }
            mActionButtom.setVisibility(View.GONE);
            parent.addView(view);
            view.setBackgroundColor(Color.BLACK);
            myView = view;
            myCallback = customViewCallback;
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
                parent.removeView(myView);
                mWebView.setVisibility(View.VISIBLE);
                mActionButtom.setVisibility(View.VISIBLE);
                if(callBack!=null){
                    callBack.onShowVideo(false);
                }
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
        inflater.inflate(R.menu.menu_detail, menu);
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
