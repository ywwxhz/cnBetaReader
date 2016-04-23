package com.ywwxhz.lib.handler;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.lib.kits.Toolkit;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2014/9/23 18:01.
 */
public abstract class BaseGsonCallback<T> extends BaseCallback<T> {

    protected Type type;

    public BaseGsonCallback(TypeToken<T> typeToken) {
        this.type = typeToken.getType();
    }

    @Override
    protected T parseResponse(Response response) throws Exception {
        return Toolkit.getGson().fromJson(response.body().string(),type);
    }
}
