package com.ywwxhz.lib.handler;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.MyApplication;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.ResponseObject;
import com.ywwxhz.lib.kits.Toolkit;

import cz.msebera.android.httpclient.Header;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/2 18:01.
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
        if(MyApplication.getInstance().getDebug()){
            Toast.makeText(getActivity(),cause.toString(),Toast.LENGTH_SHORT).show();
        }
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
        Toolkit.showCrouton(getActivity(), R.string.message_no_network, Style.ALERT);
        if(MyApplication.getInstance().getDebug()){
            Toast.makeText(getActivity(),throwable.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    protected abstract void onSuccess(T result);

    protected abstract Activity getActivity();
}
