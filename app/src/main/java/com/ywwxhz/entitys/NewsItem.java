package com.ywwxhz.entitys;

import com.google.gson.annotations.SerializedName;
import com.ywwxhz.lib.database.annotation.Id;
import com.ywwxhz.lib.database.annotation.NoAutoIncrement;

public class NewsItem {

    @Id
    @NoAutoIncrement
    private int sid;
    private String SN;
    private String thumb;
    private String largeImage;
    private String from;
    @SerializedName("url_show")
    private String url_show;
    private String title;
    private String content;
    private String hometext;
    private String summary;
    private String comments;
    private String counter;
    private String inputtime;

    public NewsItem() {
    }

    public NewsItem(int sid, String title,String url) {
        this.sid =sid;
        this.url_show = url;
        this.title = title;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHometext() {
        return hometext;
    }

    public void setHometext(String hometext) {
        this.hometext = hometext;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getInputtime() {
        return inputtime;
    }

    public void setInputtime(String inputtime) {
        this.inputtime = inputtime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl_show() {
        return url_show;
    }

    public NewsItem setUrl_show(String url_show) {
        this.url_show = url_show;
        return this;
    }
}
