package com.ywwxhz.lib.handler;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.entity.NewsItem;
import com.ywwxhz.entity.ResponseObject;
import com.ywwxhz.service.NewsListService;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class RealTimeNewListHandler extends ExternedGsonHttpResposerHandler<NewsListService, ResponseObject<ArrayList<NewsItem>>> {

    public RealTimeNewListHandler(NewsListService mHoder, TypeToken<ResponseObject<ArrayList<NewsItem>>> type) {
        super(mHoder, type);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString, ResponseObject<ArrayList<NewsItem>> object) {
        if ("success".equals(object.getState())) {
            mActionServer.callRealTimeNewsLoadSuccess(object.getResult());
        } else {
            onError(statusCode, headers, responseString, new Exception("load realtime error"));
        }
    }

    @Override
    public void onFinish() {
        mActionServer.setLoadFinish();
    }
}
