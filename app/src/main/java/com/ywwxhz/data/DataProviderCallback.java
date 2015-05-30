package com.ywwxhz.data;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 16:27.
 */
public interface DataProviderCallback<T> {
    void onLoadStart();
    void onLoadSuccess(T object);
    void onLoadFinish(int size);
    void onLoadFailure();
}
