package com.ywwxhz.lib.handler;

import org.json.JSONObject;

import okhttp3.Response;

public abstract class BaseJsonCallback extends BaseCallback<JSONObject> {

    @Override
    public JSONObject convertSuccess(Response response) throws Exception {
        return new JSONObject(response.body().string());
    }
}
