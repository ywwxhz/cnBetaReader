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
    private boolean fromDB = false;
    private NewsDetailFragment.NewsDetailCallBack callBack;

    private String webTemplate = "<!DOCTYPE html><html><head><title></title><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"/>" +
            "<link  rel=\"stylesheet\" href=\"file:///android_asset/style.css\" type=\"text/css\"/><style>.title{color: #%s;}%s</style></head>" +
            "<body><div><div class=\"title\">%s</div><div class=\"from\">%s<span style=\"float: right\">%s</span></div><div id=\"introduce\">%s<div class=\"clear\"></div></div><div class=\"content\">%s</div><div class=\"clear foot\">-- The End --</div></div>" +
            "<script>var config = {\"enableImage\":%s,\"enableFlashToHtml5\":%s};" +
            "</script><script src=\"file:///android_asset/loder.js\"></script></body></html>";
    private String night = "body{color:#9bafcb}#introduce{background-color:#262f3d;color:#616d80}.content blockquote{background-color:#262f3d;color:#616d80}";
    private String light = "#introduce{background-color:#F1F1F1;color: #444;}";
    private Handler myHandler;
    private WebSettings settings;
    private boolean shouldLoadCache;

    public NewsDetailProcesser(NewsDetailProvider provider) {
        super(provider);
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
        convertFlashToHtml5 = PrefKit.getBoolean(mActivity, R.string.pref_flash_to_html5_key, true);
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

    public void setNewsItem(int sid, String title) {
        try {
            mNewsItem = MyApplication.getInstance().getDbUtils().findById(NewsItem.class, sid);
            if (mNewsItem == null) {
                fromDB = false;
                this.mNewsItem = new NewsItem(sid, title);
            } else {
                mNewsItem.setTitle(title);
                fromDB = true;
            }
        } catch (DbException e) {
            fromDB = false;
            this.mNewsItem = new NewsItem(sid, title);
        }

    }

    @Override
    public void loadData(boolean startup) {
        String title = mNewsItem.getTitle();
        shouldLoadCache = !title.contains("直播")||title.contains("已完结");
        NewsItem mNews = mNewsItem.getSN() == null ? FileCacheKit.getInstance().getAsObject(mNewsItem.getSid() + "", NewsItem.class) : mNewsItem;
        if (mNews == null||!shouldLoadCache) {
            makeRequest();
        } else {
            hascontent = true;
            mNews.setTitle(title);
            mNewsItem = mNews;
            blindData(mNews);
        }
    }

    public void makeRequest() {
        provider.loadNewsAsync(mNewsItem.getSid() + "");
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
                    hascontent = NewsDetailProvider.handleResponceString(mNewsItem, strings[0],shouldLoadCache);
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
    public void onLoadFinish(int size) {

    }

    @Override
    public void onLoadFailure() {
        if (!hascontent) {
            loadFail.setVisibility(View.VISIBLE);
            if (callBack != null) {
                callBack.onNewsLoadFinish(mNewsItem, false);
            }
        } else {
            blindData(mNewsItem);
            mWebView.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.GONE);
        Toolkit.showCrouton(mActivity, R.string.message_no_network, Style.ALERT);
    }

    private void blindData(NewsItem mNews) {
        String colorString = Integer.toHexString(titleColor);
        String add;
        if (ThemeManger.isNightTheme(getActivity())) {
            add = night;
        } else {
            add = light;
        }
        String data = String.format(Locale.CHINA, webTemplate, colorString.substring(2, colorString.length()),
                add,mNewsItem.getTitle(),mNewsItem.getFrom(),mNewsItem.getInputtime(),mNewsItem.getHometext(),mNewsItem.getContent(), showImage, convertFlashToHtml5);
        mWebView.loadDataWithBaseURL(Configure.BASE_URL, data, "text/html", "utf-8", null);
        mWebView.setVisibility(View.VISIBLE);
        mActionButtom.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActionButtom.setVisibility(View.VISIBLE);
                mActionButtom.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            }
        }, 200);
        mProgressBar.setVisibility(View.GONE);
        if (callBack != null) {
            callBack.onNewsLoadFinish(mNewsItem, true);
        }
        if(fromDB){
            try {
                MyApplication.getInstance().getDbUtils().saveOrUpdate(mNewsItem);
            } catch (DbException ignored) {
            }
        }
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
            case 0:
                showAllImage();
                break;
        }
        return false;
    }

    private void showAllImage() {
        mWebView.loadUrl("javascript:Loader.showAllImage()");
    }

    private void nightMode() {
        mWebView.loadUrl("javascript:Loader.setNight(true)");
    }

    public void setCallBack(NewsDetailFragment.NewsDetailCallBack callBack) {
        this.callBack = callBack;
    }

    public void commentAction() {
        if (callBack != null) {
            callBack.CommentAction(mNewsItem.getSid(), mNewsItem.getSN(), mNewsItem.getTitle());
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

        @JavascriptInterface
        public void loadSohuVideo(final String hoder_id, final String requestUrl) {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    NetKit.getInstance().getClient().get(requestUrl, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                mWebView.loadUrl("javascript:Loader.VideoCallBack(\"" + hoder_id + "\",\"" + response.getJSONObject("data").getString("url_high_mp4") + "\",\"" + response.getJSONObject("data").getString("hor_big_pic") + "\")");
                            } catch (Exception e) {
                                Toolkit.showCrouton(mActivity, "搜狐视频加载失败", Style.ALERT);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toolkit.showCrouton(mActivity, "搜狐视频加载失败", Style.ALERT);
                        }
                    });
                }
            });
        }

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
            if (!TextUtils.isEmpty(prefix)) {
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(prefix);
                if (mimeType != null && mimeType.startsWith("image")) {
                    if (finish || showImage) {
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
                        System.out.println("Load Image Hoder");
                        try {
                            return new WebResourceResponse("image/svg+xml", "UTF-8", mActivity.getAssets().open("image.svg"));
                        } catch (IOException ignored) {
                        }
                    }
                } else {
                    System.out.println("load other resourse");
                }
            }
            return super.shouldInterceptRequest(view, url);
        }
    }

    class VideoWebChromeClient extends WebChromeClient {
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
                mActivity.setRequestedOrientation(orientation);
                mActivity.setRequestedOrientation(requiredOrientation);
                mActivity.getSupportActionBar().show();
                onHideHtmlVideoView(myView);
                myView = null;
            }
        }
    }

    private void onShowHtmlVideoView(View html5VideoView){

        if (callBack != null) {
            callBack.onVideoFullScreen(true);
            callBack.onShowHtmlVideoView(html5VideoView);
        }else{
            ViewGroup parent = (ViewGroup) mActivity.findViewById(R.id.content);
            parent.addView(html5VideoView);
        }
        mWebView.setVisibility(View.GONE);
        mActionButtom.setVisibility(View.GONE);
    }

    private void onHideHtmlVideoView(View html5VideoView){
        if (callBack != null) {
            callBack.onVideoFullScreen(false);
            callBack.onHideHtmlVideoView(html5VideoView);
        }else{
            ViewGroup parent = (ViewGroup) mActivity.findViewById(R.id.content);
            parent.removeView(html5VideoView);
        }
        mWebView.setVisibility(View.VISIBLE);
        mActionButtom.setVisibility(View.VISIBLE);
    }

}
