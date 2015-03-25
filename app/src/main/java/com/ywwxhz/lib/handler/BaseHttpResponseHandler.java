package com.ywwxhz.lib.handler;

import android.app.Activity;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.ResponseObject;
import com.ywwxhz.lib.kits.Toolkit;

import org.apache.http.Header;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public abstract class BaseHttpResponseHandler<T> extends GsonHttpResponseHandler<ResponseObject<T>> {

    protected BaseHttpResponseHandler(TypeToken<ResponseObject<T>> type) {
        super(type);
    }

    @Override
    protected void onError(int statusCode, Header[] headers, String responseString, Throwable cause) {
        Log.e(this.getClass().getSimpleName(), responseString + "");
        cause.printStackTrace();
        Toolkit.showCrouton(getActivity(), R.string.message_data_structure_change, Style.ALERT);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString, ResponseObject<T> object) {
        if ("success".equals(object.getState())) {
            onSuccess(object.getResult());
        } else {
            onError(statusCode, headers, responseString, new Exception("load news list fail"));
        }
    }


    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.e(this.getClass().getSimpleName(), responseString + "");
        throwable.printStackTrace();
        Crouton.makeText(getActivity(), R.string.message_no_network, Style.ALERT,R.id.content).show();
    }

    protected abstract void onSuccess(T result);

    protected abstract Activity getActivity();
}
