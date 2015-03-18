package com.ywwxhz.lib.handler;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.entitys.NewsListObject;
import com.ywwxhz.entitys.ResponseObject;
import com.ywwxhz.processers.NewsListProcesserImpl;

import org.apache.http.Header;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class NormalNewsListHandler extends ExternedGsonHttpResposerHandler<NewsListProcesserImpl, ResponseObject<NewsListObject>> {

    public NormalNewsListHandler(NewsListProcesserImpl hoder, TypeToken<ResponseObject<NewsListObject>> type) {
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
