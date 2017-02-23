package com.ywwxhz.lib.kits;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.ywwxhz.MyApplication;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.handler.BaseCallback;

import java.io.IOException;

import okhttp3.Response;


/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/10/17 21:44.
 */
public class NetKit {

    private NetKit() {
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

    public static void getNewslistByPage(Object tag, int page, String type, BaseCallback baseCallback) {
        HttpParams params = new HttpParams();
        params.put("type", type);
        params.put("page", page);
        params.put("_", System.currentTimeMillis());
        OkGo.get(Configure.NEWS_LIST_URL)
                .tag(tag)
                .params(params)
                .execute(baseCallback);
    }

    public static void getNewslistByTopic(Object tag, int page, String type, BaseCallback baseCallback) {
        HttpParams params = new HttpParams();
        params.put("type", "topic|"+type);
        params.put("page", page);
        params.put("_", System.currentTimeMillis());
        OkGo.get(Configure.NEWS_LIST_URL)
                .tag(tag)
                .params(params)
                .execute(baseCallback);
    }

    public static void getNewsBySid(Object tag, String sid, BaseCallback baseCallback) {
        OkGo.get(Configure.buildArticleUrl(sid))
                .tag(tag)
                .execute(baseCallback);
    }

    public static Response getNewsBySidSync(String sid) throws IOException {
        return OkGo.get(Configure.buildArticleUrl(sid)).execute();  //不传callback即为同步请求
    }

    public static void getCommentBySnAndSid(Object tag,String sn, String sid, BaseCallback baseCallback) {
        HttpParams params = new HttpParams();
        params.put("op", "1," + sid + "," + sn);
        OkGo.post(Configure.COMMENT_URL)
                .tag(tag)
                .params(params)
                .execute(baseCallback);
    }

    public static void setCommentAction(Object tag,String op, String sid, String tid, String csrf_token, BaseCallback baseCallback) {
        HttpParams params = new HttpParams();
        params.put("op", op);
        params.put("sid", sid);
        params.put("tid", tid);
        params.put("_csrf", csrf_token);
        OkGo.post(Configure.COMMENT_VIEW)
                .headers("Accept","application/json, text/javascript, */*; q=0.01")
                .headers("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                .tag(tag)
                .params(params)
                .execute(baseCallback);
    }

    public static boolean isNetworkConnected() {
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
