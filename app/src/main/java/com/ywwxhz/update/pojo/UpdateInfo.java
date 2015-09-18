package com.ywwxhz.update.pojo;


import android.content.Context;

import com.ywwxhz.lib.kits.FileKit;


/**
 * UpdateHelper
 *
 * Created by 远望の无限(ywwxhz) on 14-5-8 15:34.
 */
public class UpdateInfo {
    private long size;
    private String md5;
    private String apkUrl;
    private String appName;
    private int versionCode;
    private String changeLog;
    private String updateTips;
    private String versionName;
    private String publishTime;

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUpdateTips() {
        return updateTips;
    }

    public void setUpdateTips(String updateTips) {
        this.updateTips = updateTips;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String buildUpdateMessage(Context context) {
        return "应用名称: " + appName + " \n" +
                "应用版本: " + versionName + " \n" +
                "发布时间: " + publishTime + "\n" +
                "应用大小: " + FileKit.formatFileSize(context, size) + "\n" +
                "变更情况: \n" + changeLog;
    }
}
