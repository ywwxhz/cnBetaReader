package com.ywwxhz.lib.handler;

import org.json.JSONObject;

import okhttp3.Response;

public abstract class BaseJsonCallback extends BaseCallback<JSONObject> {

    @Override
    public JSONObject convertSuccess(Response response) throws Exception {
        String resp = response.body().string();
        try{
            return new JSONObject(resp);
        }catch (Exception e){
            System.out.println(resp);
            throw e;
        }
    }
}
