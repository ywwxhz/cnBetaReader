package com.ywwxhz.lib.handler;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.processers.BaseProcesserImpl;

import org.apache.http.Header;

import java.lang.ref.SoftReference;
import java.lang.reflect.Type;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public abstract class ExternedGsonHttpResposerHandler<ActionServer extends BaseProcesserImpl, T> extends GsonHttpResponseHandler<T> {
    protected SoftReference<ActionServer> mActionServer;
    protected Type type;

    protected ExternedGsonHttpResposerHandler(ActionServer mActionServer, TypeToken<T> type) {
        this.mActionServer = new SoftReference<>(mActionServer);
        this.type = type.getType();
    }

    @Override
    protected void onError(int statusCode, Header[] headers, String responseString, Throwable cause) {
        Log.e(this.getClass().getSimpleName(), responseString + "");
        cause.printStackTrace();
        Crouton.makeText(mActionServer.get().getActivity(), R.string.message_data_structure_change, Style.ALERT).show();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.e(this.getClass().getSimpleName(), responseString + "");
        throwable.printStackTrace();
        Crouton.makeText(mActionServer.get().getActivity(), R.string.message_no_network, Style.ALERT).show();
    }

    @Override
    public Type getType() {
        return type;
    }
}
