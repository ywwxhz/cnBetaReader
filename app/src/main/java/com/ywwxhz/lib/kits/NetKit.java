package com.ywwxhz.lib.kits;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.ywwxhz.lib.Configure;

import org.apache.http.Header;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicHeader;

/**
 * Created by ywwxhz on 2014/10/17.
 */
public class NetKit {

    private AsyncHttpClient mClient;
    private Context mContext;

    public NetKit(Context context) {
        this.mContext = context;
        mClient = new AsyncHttpClient();
        mClient.setCookieStore(new BasicCookieStore());
        mClient.setUserAgent("Mozilla/5.0 (Linux; Android 4.2.1; zh-CN; Nexus 4 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
    }

    public static int getConnectedType(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
            return mNetworkInfo.getType();
        }
        return -1;
    }

    public void getNewslistByPage(String sid, int page, ResponseHandlerInterface handlerInterface) {
        mClient.get(mContext, Configure.buildNewsListUrl("all", page + "", sid), getAuthHeader(), null, handlerInterface);
    }

    public void getRealtimeNews(String sid, ResponseHandlerInterface handlerInterface) {
        mClient.get(mContext, Configure.buildNewsListUrl("realtime", "1", sid), getAuthHeader(), null, handlerInterface);
    }

    public void getNewsBySid(String sid, ResponseHandlerInterface handlerInterface) {
        mClient.get(mContext, Configure.buildArticleUrl(sid), getAuthHeader(), null, handlerInterface);
    }

    public void getTopicComment(String page, ResponseHandlerInterface handlerInterface) {
        mClient.get(mContext, Configure.buildNewsListUrl("jhcomment",page,""), getAuthHeader(), null, handlerInterface);
    }

    public void getCommentBySnAndSid(String sn, String sid, ResponseHandlerInterface handlerInterface) {
        mClient.get(mContext, Configure.buildCommentUrl(sid, sn), getAuthHeader(), null, handlerInterface);
    }

    public void setCommentAction(String op, String sid, String tid ,String csrf_token, ResponseHandlerInterface handlerInterface) {
        RequestParams params = new RequestParams();
        params.add("op",op);
        params.add("sid",sid);
        params.add("tid",tid);
        params.add("csrf_token",csrf_token);
        mClient.post(mContext, Configure.COMMENT_VIEW, getAuthHeader(), params,"application/x-www-form-urlencoded; charset=UTF-8", handlerInterface);
    }

    private Header[] getAuthHeader() {
        return new Header[]{new BasicHeader("Referer", "http://www.cnbeta.com/"), new BasicHeader("X-Requested-With", "XMLHttpRequest")};
    }

    public AsyncHttpClient getClient() {
        return mClient;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext
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

    public boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        return networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public boolean isMobileConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        return networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }
}
