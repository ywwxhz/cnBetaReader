package com.ywwxhz.lib.handler;

import org.json.JSONObject;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：默认将返回的数据解析成需要的Bean,可以是 BaseBean，String，List，Map
 * 修订历史：
 * ================================================
 */
public abstract class BaseJsonCallback extends BaseCallback<JSONObject> {
    @Override
    protected JSONObject parseResponse(Response response) throws Exception {
        return new JSONObject(response.body().string());
    }
}
