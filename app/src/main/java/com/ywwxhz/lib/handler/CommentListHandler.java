package com.ywwxhz.lib.handler;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.entity.CommentListObject;
import com.ywwxhz.entity.ResponseObject;
import com.ywwxhz.processer.NewsCommentProcesser;

import org.apache.http.Header;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public class CommentListHandler extends ExternedGsonHttpResposerHandler<NewsCommentProcesser, ResponseObject<CommentListObject>> {

    public CommentListHandler(NewsCommentProcesser newsCommentProcesser, TypeToken<ResponseObject<CommentListObject>> type) {
        super(newsCommentProcesser, type);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString, ResponseObject<CommentListObject> object) {
        mActionServer.get().callOnLoadingSuccess(object.getResult(), false, false);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        if (!mActionServer.get().callOnFailure(false, false)) {
            super.onFailure(statusCode, headers, responseString, throwable);
        }
    }

    @Override
    protected void onError(int statusCode, Header[] headers, String responseString, Throwable cause) {
        super.onError(statusCode, headers, responseString, cause);
        mActionServer.get().callOnFailure(true, true);
    }

    @Override
    public void onFinish() {
        mActionServer.get().setLoadFinish();
    }
}
