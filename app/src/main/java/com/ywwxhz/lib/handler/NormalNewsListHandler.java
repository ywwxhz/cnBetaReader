package com.ywwxhz.lib.handler;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.entity.NewsListObject;
import com.ywwxhz.entity.ResponseObject;
import com.ywwxhz.processer.NewsListProcesser;

import org.apache.http.Header;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class NormalNewsListHandler extends ExternedGsonHttpResposerHandler<NewsListProcesser, ResponseObject<NewsListObject>> {

    public NormalNewsListHandler(NewsListProcesser hoder, TypeToken<ResponseObject<NewsListObject>> type) {
        super(hoder, type);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString, ResponseObject<NewsListObject> object) {
        if ("success".equals(object.getState())) {
            mActionServer.get().callNewsPageLoadSuccess(object.getResult());
        } else {
            onError(statusCode, headers, responseString, new Exception("load news list fail"));
        }
    }

    @Override
    public void onFinish() {
        mActionServer.get().setLoadFinish();
    }
}
