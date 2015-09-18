package com.ywwxhz.lib.kits;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;
import com.ywwxhz.MyApplication;
import com.ywwxhz.lib.Configure;

import org.apache.http.Header;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicHeader;

/**
 * Created by ywwxhz on 2014/10/17.
 */
public class NetKit {

    private static NetKit instance = new NetKit();
    private AsyncHttpClient mAsyncHttpClient;
    private SyncHttpClient mSyncHttpClient;
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";

    private NetKit() {
        mAsyncHttpClient = new AsyncHttpClient();
        mSyncHttpClient = new SyncHttpClient();
        setupHttpClient(mAsyncHttpClient);
        setupHttpClient(mSyncHttpClient);
    }

    private void setupHttpClient(AsyncHttpClient client) {
        client.setCookieStore(new BasicCookieStore());
        client.setConnectTimeout(3000);
        client.setResponseTimeout(6000);
        client.setMaxRetriesAndTimeout(3, 200);
        client.setUserAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.45 Safari/537.36");
    }

    public static int getConnectedType() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
            return mNetworkInfo.getType();
        }
        return -1;
    }

    public static NetKit getInstance() {
        return instance;
    }

    public static AsyncHttpClient getAsyncClient() {
        return instance.mAsyncHttpClient;
    }

    public static SyncHttpClient getSyncClient() {
        return instance.mSyncHttpClient;
    }

    public void getNewslistByPage(int page, String type, ResponseHandlerInterface handlerInterface) {
        RequestParams params = new RequestParams();
        params.add("type", type);
        params.add("page", page + "");
        params.add("_", System.currentTimeMillis() + "");
        mAsyncHttpClient.get(null, Configure.NEWS_LIST_URL, getAuthHeader(), params, handlerInterface);
    }

    public void getNewslistByTopic(int page, String type, ResponseHandlerInterface handlerInterface) {
        RequestParams params = new RequestParams();
        params.add("id", type);
        params.add("page", page + "");
        params.add("_", System.currentTimeMillis() + "");
        mAsyncHttpClient.get(null, Configure.TOPIC_NEWS_LIST, getAuthHeader(), params, handlerInterface);
    }

    public void getNewsBySid(String sid, ResponseHandlerInterface handlerInterface) {
        mAsyncHttpClient.get(Configure.buildArticleUrl(sid), handlerInterface);
    }

    public void getNewsBySidSync(String sid, ResponseHandlerInterface handlerInterface) {
        mSyncHttpClient.get(Configure.buildArticleUrl(sid), handlerInterface);
    }

    public void getCommentBySnAndSid(String sn, String sid, ResponseHandlerInterface handlerInterface) {
        RequestParams params = new RequestParams();
        params.add("op", "1," + sid + "," + sn);
        mAsyncHttpClient.post(null, Configure.COMMENT_URL, getAuthHeader(), params, CONTENT_TYPE, handlerInterface);
    }

    public void setCommentAction(String op, String sid, String tid, String csrf_token, ResponseHandlerInterface handlerInterface) {
        RequestParams params = new RequestParams();
        params.add("op", op);
        params.add("sid", sid);
        params.add("tid", tid);
        params.add("csrf_token", csrf_token);
        mAsyncHttpClient.post(null, Configure.COMMENT_VIEW, getAuthHeader(), params, CONTENT_TYPE, handlerInterface);
    }

    public static Header[] getAuthHeader() {
        return new Header[]{
                new BasicHeader("Referer", "http://www.cnbeta.com/"),
                new BasicHeader("Origin", "http://www.cnbeta.com"),
                new BasicHeader("X-Requested-With", "XMLHttpRequest")
        };
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        return networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isMobileConnected() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        return networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static int getNetType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnected())) {
            return 0;
        }
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {
            return 1;
        } else {
            return 2;
        }
    }
}
