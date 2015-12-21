package com.ywwxhz.lib;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.VideoView;

public class BaseWebChromeClient extends WebChromeClient implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    boolean mIsVideoFullscreen = false;
    FrameLayout mVideoViewFrameLayout;
    CustomViewCallback mVideoViewCallback;

    Callback mOutCallback;

    WebView mWebView;
    View mNonFullscreenVideoLayout;
    ViewGroup mFullscreenVideoLayout;

    public interface Callback {
        void enterFullscreenVideo();

        void exitFullscreenVideo();
    }

    class JavaScriptInterface {

        WebChromeClient mWebChromeClient;

        public JavaScriptInterface(WebChromeClient webChromeClient) {
            mWebChromeClient = webChromeClient;
        }

        @android.webkit.JavascriptInterface
        @SuppressWarnings("unused")
        public void notifyVideoEnd() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (mWebChromeClient != null) {
                        mWebChromeClient.onHideCustomView();
                    }
                }
            });
        }
    }

    public void init(WebView webview, View nonFullscreenVideoLayout,
            ViewGroup fullScreenVideoLayout,
            Callback callback) {
        mWebView = webview;
        mNonFullscreenVideoLayout = nonFullscreenVideoLayout;
        mFullscreenVideoLayout = fullScreenVideoLayout;
        mOutCallback = callback;

        webview.addJavascriptInterface(new JavaScriptInterface(this),
                "_VideoEnabledWebView");
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {

        if (view instanceof FrameLayout) {
            // A video wants to be shown
            FrameLayout frameLayout = (FrameLayout) view;
            View focusedChild = frameLayout.getFocusedChild();

            // Save video related variables
            mIsVideoFullscreen = true;
            mVideoViewFrameLayout = frameLayout;
            mVideoViewCallback = callback;

            // Hide the non-video view, add the video view, and show it
            mNonFullscreenVideoLayout.setVisibility(View.INVISIBLE);
            mFullscreenVideoLayout.addView(mVideoViewFrameLayout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullscreenVideoLayout.setVisibility(View.VISIBLE);

            if (focusedChild instanceof android.widget.VideoView) {
                // android.widget.VideoView (typically API level <11)
                VideoView videoView = (VideoView) focusedChild;

                // Handle all the required events
                videoView.setOnPreparedListener(this);
                videoView.setOnCompletionListener(this);
                videoView.setOnErrorListener(this);
            } else {
                // Other classes, including:
                // - android.webkit.HTML5VideoFullScreen$VideoSurfaceView,
                // which inherits from android.view.SurfaceView (typically
                // API level 11-18)
                // - android.webkit.HTML5VideoFullScreen$VideoTextureView,
                // which inherits from android.view.TextureView (typically
                // API level 11-18)
                // -
                // com.android.org.chromium.content.browser.ContentVideoView$VideoSurfaceView,
                // which inherits from android.view.SurfaceView (typically
                // API level 19+)

                // Handle HTML5 video ended event only if the class is a
                // SurfaceView
                // Test case: TextureView of Sony Xperia T API level 16
                // doesn't work fullscreen when loading the javascript below
                if (mWebView != null && mWebView.getSettings().getJavaScriptEnabled()
                        && focusedChild instanceof SurfaceView) {
                    // Run javascript code that detects the video end and
                    // notifies the Javascript interface
                    String js = "javascript:";
                    js += "var _ytrp_html5_video_last;";
                    js += "var _ytrp_html5_video = document.getElementsByTagName('video')[0];";
                    js += "if (_ytrp_html5_video != undefined && _ytrp_html5_video != _ytrp_html5_video_last) {";
                    {
                        js += "_ytrp_html5_video_last = _ytrp_html5_video;";
                        js += "function _ytrp_html5_video_ended() {";
                        {
                            js += "_VideoEnabledWebView.notifyVideoEnd();";
                        }
                        js += "}";
                        js += "_ytrp_html5_video.addEventListener('ended', _ytrp_html5_video_ended);";
                    }
                    js += "}";

                    injectJS(mWebView, js);
                }
            }

            // Notify full-screen change
            if (mOutCallback != null) {
                mOutCallback.enterFullscreenVideo();
            }
        }

    }

    @Override
    public void onHideCustomView() {
        // This method should be manually called on video end in all cases
        // because it's not always called automatically.
        // This method must be manually called on back key press (from this
        // class' onBackPressed() method).

        if (mIsVideoFullscreen) {
            // Hide the video view, remove it, and show the non-video view
            mFullscreenVideoLayout.setVisibility(View.INVISIBLE);
            mFullscreenVideoLayout.removeView(mVideoViewFrameLayout);
            mNonFullscreenVideoLayout.setVisibility(View.VISIBLE);

            // Call back (only in API level <19, because in API level 19+ with
            // chromium webview it crashes)
            if (mVideoViewCallback != null
                    && !mVideoViewCallback.getClass().getName().contains(".chromium.")) {
                mVideoViewCallback.onCustomViewHidden();
            }

            // Reset video related variables
            mIsVideoFullscreen = false;
            mVideoViewFrameLayout = null;
            mVideoViewCallback = null;

            // Notify full-screen change
            if (mOutCallback != null) {
                mOutCallback.exitFullscreenVideo();
            }
        }
    }

    private void injectJS(WebView webview, String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webview.evaluateJavascript(script, null);
        } else {
            webview.loadUrl("javascript:" + script);
        }
    }

    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        onHideCustomView();
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {

    }

    public boolean onBackPressed() {
        if (mIsVideoFullscreen) {
            onHideCustomView();
            return true;
        } else {
            return false;
        }
    }

}