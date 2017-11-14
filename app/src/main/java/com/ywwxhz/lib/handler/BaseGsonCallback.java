package com.ywwxhz.lib.handler;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.lib.kits.Toolkit;

import java.lang.reflect.Type;


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
    public T convertResponse(okhttp3.Response response) throws Throwable {
        return Toolkit.getGson().fromJson(beforeConvertSuccess(response.body().string()), type);
    }

    /**
     * 在执行 convertSuccess(Response response) 之前预先对请求做处理<br>
     * 默认返回请求内容，不做修改
     * 
     * @param body
     *            服务器端返回内容
     * @return 处理后的内容
     */
    protected String beforeConvertSuccess(String body) throws Exception {
        return body;
    }
}
