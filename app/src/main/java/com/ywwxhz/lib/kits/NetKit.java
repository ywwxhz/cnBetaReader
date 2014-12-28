package com.ywwxhz.lib.kits;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.ywwxhz.app.MyApplication;
import com.ywwxhz.lib.Configure;

import org.apache.http.Header;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicHeader;

import java.net.SocketTimeoutException;

/**
 * Created by ywwxhz on 2014/10/17.
 */
public class NetKit {

    private static NetKit instance;
    private AsyncHttpClient mClient;
    private static final String CONTENT_TYPE="application/x-www-form-urlencoded; charset=UTF-8";

    static {
        AsyncHttpClient.allowRetryExceptionClass(SocketTimeoutException.class);
    }

    private NetKit() {
        mClient = new AsyncHttpClient();
        mClient.setCookieStore(new BasicCookieStore());
        mClient.setUserAgent("Mozilla/5.0 (Linux; Android 4.2.1; zh-CN; Nexus 4 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
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
        if(instance == null){
            instance = new NetKit();
        }
        return instance;
    }

    public void getNewslistByPage(int page, ResponseHandlerInterface handlerInterface) {
        RequestParams params = new RequestParams();
        params.add("type","all");
        params.add("page",page+"");
        params.add("_",System.currentTimeMillis()+"");
        mClient.get(MyApplication.getInstance(), Configure.NEWS_LIST_URL, getAuthHeader(), params, handlerInterface);
    }

    public void getNewsBySid(String sid, ResponseHandlerInterface handlerInterface) {
        mClient.get(MyApplication.getInstance(), Configure.buildArticleUrl(sid), handlerInterface);
    }

    public void getCommentBySnAndSid(String sn, String sid, ResponseHandlerInterface handlerInterface) {
        RequestParams params = new RequestParams();
        params.add("op","1,"+sid+","+sn);
        mClient.post(MyApplication.getInstance(), Configure.COMMENT_URL, getAuthHeader(), params,CONTENT_TYPE, handlerInterface);
    }

    public void setCommentAction(String op, String sid, String tid ,String csrf_token, ResponseHandlerInterface handlerInterface) {
        RequestParams params = new RequestParams();
        params.add("op",op);
        params.add("sid",sid);
        params.add("tid",tid);
        params.add("csrf_token",csrf_token);
        mClient.post(MyApplication.getInstance(), Configure.COMMENT_VIEW, getAuthHeader(), params,CONTENT_TYPE, handlerInterface);
    }

    private Header[] getAuthHeader() {
        return new Header[]{
                new BasicHeader("Referer", "http://www.cnbeta.com/"),
                new BasicHeader("Accept","application/json, text/javascript, */*; q=0.01"),
                new BasicHeader("Accept-Encoding","gzip, deflate"),
                new BasicHeader("Origin","http://www.cnbeta.com"),
                new BasicHeader("X-Requested-With", "XMLHttpRequest")
        };
    }

    public AsyncHttpClient getClient() {
        return mClient;
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
}
