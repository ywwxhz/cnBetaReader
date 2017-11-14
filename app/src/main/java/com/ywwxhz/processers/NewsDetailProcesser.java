package com.ywwxhz.processers;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.base.Request;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ywwxhz.MyApplication;
import com.ywwxhz.activitys.ImageViewActivity;
import com.ywwxhz.activitys.NewsDetailActivity;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.DataProviderCallback;
import com.ywwxhz.data.impl.NewsDetailProvider;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.fragments.NewsDetailFragment;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.CroutonStyle;
import com.ywwxhz.lib.ScrollToTopCliclListiner;
import com.ywwxhz.lib.ThemeManger;
import com.ywwxhz.lib.database.exception.DbException;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.Toolkit;
import com.ywwxhz.widget.AVLoadingIndicatorView.AVLoadingIndicatorView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;

import de.keyboardsurfer.android.widget.crouton.Style;
import okhttp3.Response;

/**
 * cnBetaReader Created by 远望の无限(ywwxhz) on 2014/11/1 17:48.
 */
public class NewsDetailProcesser extends BaseProcesserImpl<NewsItem, NewsDetailProvider>
        implements DataProviderCallback<NewsItem> {
    private View loadFail;
    private WebView mWebView;
    private boolean hascontent;
    private NewsItem mNewsItem;
    private AVLoadingIndicatorView mProgressBar;
    private FloatingActionButton mActionButtom;
    private VideoWebChromeClient client = new VideoWebChromeClient();
    private boolean showImage;
    private boolean convertFlashToHtml5;
    private boolean fromDB = false;
    private NewsDetailFragment.NewsDetailCallBack callBack;

    private String webTemplate = "<!DOCTYPE html><html><head><base href=\"http://www.cnbeta.com/\" /><title></title><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"/>"
            + "<link  rel=\"stylesheet\" href=\"file:///android_asset/style.css\" type=\"text/css\"/><style>.title{color: #%s;}</style>"
            + "<script>var config = {\"enableImage\":%s,\"enableFlashToHtml5\":%s,staticLoading:%s};</script>"
            + "<script src=\"file:///android_asset/js/BaseTool.js\"></script>"
            + "<script src=\"file:///android_asset/js/ImageTool.js\"></script>"
            + "<script src=\"file:///android_asset/js/VideoTool.js\"></script></head>"
            + "<body class='%s'><div><div class=\"title\">%s</div><div class=\"from\">%s<span style=\"float: right;margin-top: 3pt;\">%s</span></div><div id=\"introduce\">%s<div class=\"clear\"></div></div><div id=\"content\">%s</div><div class=\"clear foot\">-- The End --</div></div>"
            + "<script src=\"file:///android_asset/js/loder.js\"></script></body></html>";
    private Handler myHandler;
    private WebSettings settings;
    private boolean shouldLoadCache;
    private boolean showBlockAd = false;
    private boolean svgLoading = false;
    private View.OnClickListener scrollToTop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mWebView.scrollTo(0, 0);
        }
    };

    public NewsDetailProcesser(NewsDetailProvider provider) {
        super(provider);
    }

    /**
     * 隐藏进度条
     */
    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        mWebView.onPause();
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
        if (!PrefKit.getBoolean(mActivity, R.string.pref_auto_image_key, true)) {
            showImage = PrefKit.getBoolean(mActivity, R.string.pref_show_detail_image_key, true);
        } else {
            showImage = NetKit.isWifiConnected();
        }
        svgLoading = PrefKit.getBoolean(mActivity, R.string.pref_svg_loading_key, false);
        convertFlashToHtml5 = PrefKit.getBoolean(mActivity, R.string.pref_flash_to_html5_key, true);
    }

    @SuppressLint({ "AddJavascriptInterface", "SetJavaScriptEnabled" })
    private void initView(View view) {
        this.loadFail = view.findViewById(R.id.message);
        this.mWebView = (WebView) view.findViewById(R.id.webview);
        if (ThemeManger.isNightTheme(getActivity())) {
            this.mWebView.setBackgroundColor(windowBackground);
        }
        this.mProgressBar = (AVLoadingIndicatorView) view.findViewById(R.id.loading);
        this.mActionButtom = (FloatingActionButton) view.findViewById(R.id.action);
        this.mActionButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentAction();
            }
        });
        mActionButtom.setScaleX(0);
        mActionButtom.setScaleY(0);
        mActionButtom.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                scrollToTop.onClick(v);
                return true;
            }
        });
        settings = mWebView.getSettings();
        // android 5.0 以上版本Webview设置允许使用第三方COOKIE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptCookie(true);
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setSupportZoom(false);
        settings.setAllowContentAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
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

    public void setNewsItem(int sid, String title, String url) {
        try {
            mNewsItem = MyApplication.getInstance().getDbUtils().findById(NewsItem.class, sid);
            if (mNewsItem == null) {
                fromDB = false;
                this.mNewsItem = new NewsItem(sid, title, url);
            } else {
                if (title.length() > 0) {
                    mNewsItem.setTitle(title);
                }
                fromDB = true;
            }
        } catch (DbException e) {
            fromDB = false;
            this.mNewsItem = new NewsItem(sid, title, url);
        }

    }

    @Override
    public void loadData(boolean startup) {
        String title = mNewsItem.getTitle();
        shouldLoadCache = !title.contains("直播") || title.contains("已完结");
        NewsItem mNews = mNewsItem.getSN() == null
                ? FileCacheKit.getInstance().getAsObject(mNewsItem.getSid() + "", NewsItem.class) : mNewsItem;
        if (mNews == null || !shouldLoadCache) {
            makeRequest();
        } else {
            hascontent = true;
            if (title.length() > 0) {
                mNews.setTitle(title);
            }
            mNewsItem = mNews;
            bindData();
        }
    }

    public void makeRequest() {
        provider.loadNewsAsync(mNewsItem);
    }

    @Override
    public void onLoadStart() {
        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
        loadFail.setVisibility(View.GONE);
    }

    @Override
    public void onLoadSuccess(NewsItem newsItem) {
        bindData();
        hascontent = true;
    }

    @Override
    public void onLoadFinish(int size) {

    }

    @Override
    public void setActivity(AppCompatActivity activity) {
        super.setActivity(activity);
        setUserVisibleHint(true);
    }

    @Override
    public void onLoadFailure() {
        if (!hascontent) {
            loadFail.setVisibility(View.VISIBLE);
            if (callBack != null) {
                callBack.onNewsLoadFinish(mNewsItem, false);
            }
        } else {
            bindData();
            mWebView.setVisibility(View.VISIBLE);
        }
        hideProgressBar();
        Toolkit.showCrouton(mActivity, R.string.message_no_network, Style.ALERT);
    }

    private void bindData() {
        String colorString = Integer.toHexString(titleColor);
        String add;
        if (ThemeManger.isNightTheme(getActivity())) {
            add = "night";
        } else {
            add = "light";
        }
        mActivity.setTitle(mNewsItem.getTitle());
        String data = String.format(Locale.CHINA, webTemplate, colorString.substring(2, colorString.length()),
                showImage, convertFlashToHtml5, !svgLoading, add, mNewsItem.getTitle(), mNewsItem.getFrom(),
                mNewsItem.getInputtime(), mNewsItem.getHometext(), mNewsItem.getContent());
        mWebView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);

        mActionButtom.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onNewsLoadFinish(mNewsItem, true);
                }
                if (fromDB) {
                    try {
                        MyApplication.getInstance().getDbUtils().saveOrUpdate(mNewsItem);
                    } catch (DbException ignored) {
                    }
                }
                mActionButtom.setVisibility(View.VISIBLE);
                mActionButtom.animate().scaleX(1).scaleY(1).setDuration(500)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).start();
                hideProgressBar();
                mWebView.setVisibility(View.VISIBLE);
            }
        }, 200);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        if (!showImage) {
            menu.add(0, 0, 0, "显示全部图片");
        }
        if (fromDB) {
            menu.findItem(R.id.menu_book_mark).setTitle("取消收藏");
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
            doBookmark(item);
            break;
        case R.id.menu_block_ad:
            traggerBlock(item);
            break;
        case 0:
            showAllImage();
            break;
        }
        return false;
    }

    /**
     * 是否显示屏蔽的元素
     *
     * @param item
     */
    private void traggerBlock(MenuItem item) {
        showBlockAd = !showBlockAd;
        if (showBlockAd) {
            item.setTitle(R.string.menu_hide_block_ad);
        } else {
            item.setTitle(R.string.menu_show_block_ad);
        }
        mWebView.loadUrl("javascript:BaseTool.traggleBlock(" + showBlockAd + ")");
    }

    /**
     * 显示所有图片
     */
    private void showAllImage() {
        mWebView.loadUrl("javascript:ImageTool.showAllImage()");
    }

    /**
     * 切换夜间模式
     */
    private void nightMode() {
        mWebView.loadUrl("javascript:BaseTool.setNight(true)");
    }

    public void setCallBack(NewsDetailFragment.NewsDetailCallBack callBack) {
        this.callBack = callBack;
    }

    public void commentAction() {
        if (callBack != null) {
            callBack.commentAction(mNewsItem.getSid(), mNewsItem.getSN(), mNewsItem.getTitle());
        }
    }

    public void shareAction() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                mActivity.getString(R.string.share_templates, mNewsItem.getTitle(),
                        TextUtils.isEmpty(mNewsItem.getUrl_show()) ? Configure.buildArticleUrl(mNewsItem.getSid() + "")
                                : mNewsItem.getUrl_show()));
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
        final DiscreteSeekBar discreteSeekBar = (DiscreteSeekBar) LayoutInflater.from(mActivity)
                .inflate(R.layout.fragment_font_size, null);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    PrefKit.writeInt(getActivity(), "font_size", discreteSeekBar.getProgress());
                    break;
                case AlertDialog.BUTTON_NEUTRAL:
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    settings.setTextZoom(100);
                    PrefKit.delete(getActivity(), "font_size");
                    break;
                }
                dialog.dismiss();
            }
        };
        discreteSeekBar.setProgress(settings.getTextZoom());
        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
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
        new AlertDialog.Builder(mActivity).setTitle("字体大小").setView(discreteSeekBar).setPositiveButton("保存", listener)
                .setNegativeButton("默认", listener).setNeutralButton("取消", listener).create().show();
    }

    /**
     * 收藏新闻
     *
     * @param item
     */
    public void doBookmark(MenuItem item) {
        if (hascontent) {
            String message;
            Style style;
            try {
                if (MyApplication.getInstance().getDbUtils().findById(NewsItem.class, mNewsItem.getSid()) == null) {
                    MyApplication.getInstance().getDbUtils().saveOrUpdate(mNewsItem);
                    message = "收藏成功";
                    item.setTitle("取消收藏");
                    fromDB = true;
                } else {
                    MyApplication.getInstance().getDbUtils().deleteById(NewsItem.class, mNewsItem.getSid());
                    message = "取消收藏成功";
                    item.setTitle("收藏");
                    fromDB = false;
                }
                style = CroutonStyle.INFO;
            } catch (DbException e) {
                message = "操作失败";
                style = Style.ALERT;
            }
            Toolkit.showCrouton(mActivity, message, style);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && client.myCallback != null) {
            client.onHideCustomView();
            return true;
        }
        return false;
    }

    private void onShowHtmlVideoView(View html5VideoView) {

        if (callBack != null) {
            callBack.onVideoFullScreen(true);
            callBack.onShowHtmlVideoView(html5VideoView);
        } else {
            ViewGroup parent = (ViewGroup) mActivity.findViewById(R.id.content);
            parent.addView(html5VideoView);
        }
        mWebView.setVisibility(View.GONE);
        mActionButtom.setVisibility(View.GONE);
    }

    private void onHideHtmlVideoView(View html5VideoView) {
        if (callBack != null) {
            callBack.onVideoFullScreen(false);
            callBack.onHideHtmlVideoView(html5VideoView);
        } else {
            ViewGroup parent = (ViewGroup) mActivity.findViewById(R.id.content);
            parent.removeView(html5VideoView);
        }
        mWebView.setVisibility(View.VISIBLE);
        mActionButtom.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mWebView.loadUrl("javascript:BaseTool.updateWidth()");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (getActivity() != null && getActivity() instanceof ScrollToTopCliclListiner && isVisibleToUser) {
            ((ScrollToTopCliclListiner) getActivity()).attachCallBack(scrollToTop);
        }
    }

    private class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showImage(String pos, final String[] imageSrcs) {
            final int posi;
            try {
                posi = Integer.parseInt(pos);
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mContext, ImageViewActivity.class);
                        intent.putExtra(ImageViewActivity.IMAGE_URLS, imageSrcs);
                        intent.putExtra(ImageViewActivity.CURRENT_POS, posi);
                        mContext.startActivity(intent);
                    }
                });
            } catch (Exception e) {
                Log.d(getClass().getName(), "Illegal argument");
            }
        }

        /**
         * 加载搜狐视屏
         *
         * @param hoder_id
         *            位置
         * @param requestUrl
         *            请求地址
         */
        @JavascriptInterface
        public void loadSohuVideo(final String hoder_id, final String requestUrl) {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    OkGo.get(requestUrl).execute(new Callback<Object>() {
                        @Override
                        public void onStart(Request<Object, ? extends Request> request) {

                        }

                        @Override
                        public void onSuccess(com.lzy.okgo.model.Response<Object> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                mWebView.loadUrl("javascript:VideoTool.VideoCallBack(\"" + hoder_id + "\",\""
                                        + jsonObject.getJSONObject("data").getString("url_high_mp4") + "\",\""
                                        + jsonObject.getJSONObject("data").getString("hor_big_pic") + "\")");
                            } catch (Exception e) {
                                Toolkit.showCrouton(mActivity, "搜狐视频加载失败", Style.ALERT);
                            }
                        }

                        @Override
                        public void onCacheSuccess(com.lzy.okgo.model.Response<Object> response) {

                        }

                        @Override
                        public void onError(com.lzy.okgo.model.Response<Object> response) {
                            Toolkit.showCrouton(mActivity, "搜狐视频加载失败", Style.ALERT);
                            if (response.getException() != null) {
                                if (MyApplication.getInstance().getDebug()) {
                                    Toast.makeText(getActivity(), response.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void uploadProgress(Progress progress) {

                        }

                        @Override
                        public void downloadProgress(Progress progress) {

                        }

                        @Override
                        public Object convertResponse(Response response) throws Throwable {
                            return response.body().toString();
                        }
                    });
                }
            });
        }

        /**
         * 显示消息
         *
         * @param message
         *            消息名称
         * @param type
         *            消息类型
         */
        @JavascriptInterface
        public void showMessage(final String message, final String type) {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toolkit.showCrouton(mActivity, message, CroutonStyle.getStyle(type));
                }
            });
        }

    }

    class MyWebViewClient extends WebViewClient {


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                Intent intent;
                Matcher sidMatcher = Configure.ARTICLE_PATTERN.matcher(url);
                if (sidMatcher.find()) {
                    intent = new Intent(mActivity, NewsDetailActivity.class);
                    intent.putExtra(NewsDetailFragment.NEWS_URL_KEY, url);
                    intent.putExtra(NewsDetailFragment.NEWS_TITLE_KEY, "");
                    mActivity.startActivity(intent);
                    mActivity.finish();
                } else {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                    mActivity.startActivity(intent);
                }
            } catch (Exception ignored) {

            }
            return true;
        }

        @Override
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return shouldInterceptRequest(view, request.getUrl().toString());
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (url.startsWith("file")) {
                return null;
            }
            // 屏蔽优酷广告
            if (url.matches(".*((atm.youku.com)|(admaster.com.cn)).*")) {
                System.out.println("MyWebViewClient.Block " + url);
                try {
                    return new WebResourceResponse("text/plain", "UTF-8", mActivity.getAssets().open("empty"));
                } catch (IOException ignored) {
                }
            }
            System.out.println("MyWebViewClient.shouldInterceptRequest(view,url) url = [" + url + "]");
            String prefix = MimeTypeMap.getFileExtensionFromUrl(url);
            if (!TextUtils.isEmpty(prefix)) {
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(prefix);
                if (mimeType != null && mimeType.startsWith("image")) {
                    // if (finish || showImage) {
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
                } else {
                    System.out.println("load other resourse");
                }
            }
            return super.shouldInterceptRequest(view, url);
        }
    }

    class VideoWebChromeClient extends WebChromeClient {
        CustomViewCallback myCallback = null;
        private View myView = null;

        @Override
        public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
            if (myCallback != null) {
                myCallback.onCustomViewHidden();
                myCallback = null;
                return;
            }
            view.setBackgroundColor(Color.BLACK);
            onShowHtmlVideoView(view);
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

                onHideHtmlVideoView(myView);
                myView = null;
            }
        }
    }
}
