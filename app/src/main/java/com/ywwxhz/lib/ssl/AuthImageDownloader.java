package com.ywwxhz.lib.ssl;

import android.content.Context;

import com.lzy.okgo.OkGo;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

public class AuthImageDownloader extends BaseImageDownloader {

    public AuthImageDownloader(Context context) {
        this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);
    }

    public AuthImageDownloader(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
    }

    @Override
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        Response execute = OkGo.get(imageUri).execute();
        return new FlushedInputStream(new BufferedInputStream(execute.body().byteStream()));
    }
}