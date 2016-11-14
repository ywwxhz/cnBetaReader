package com.ywwxhz.data;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 16:27.
 */
public interface DataProviderCallback<T> {
    /**
     * 开始加载
     */
    void onLoadStart();

    /**
     * 加载成功
     *
     * @param object 加载成功后的结果集
     */
    void onLoadSuccess(T object);

    /**
     * 加载完成
     *
     * @param size 加载数量
     */
    void onLoadFinish(int size);

    /**
     * 加载失败
     */
    void onLoadFailure();
}
