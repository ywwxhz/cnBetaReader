package com.ywwxhz.entity;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class NewsItem implements Serializable {

    @Expose
    private int sid;
    private String SN;
    @Expose
    private String aid;
    private String icon;
    @Expose
    private String thumb;
    private String from;
    @Expose
    private String title;
    private String content;
    @Expose
    private String hometext;
    @Expose
    private int counter;
    @Expose
    private String inputtime;

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

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
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

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getInputtime() {
        return inputtime;
    }

    public void setInputtime(String inputtime) {
        this.inputtime = inputtime;
    }
}
