package com.ywwxhz.entitys;

import com.ywwxhz.lib.database.annotation.Id;
import com.ywwxhz.lib.database.annotation.NoAutoIncrement;

public class NewsHistroyItem {

    @Id
    @NoAutoIncrement
    private int sid;
    private String thumb;
    private String largeImage;
    private String title;
    private String hometext;
    private String summary;
    private long viewTime;

    public NewsHistroyItem() {
    }

    public NewsHistroyItem(int sid, String title) {
        this.sid = sid;
        this.title = title;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHometext() {
        return hometext;
    }

    public void setHometext(String hometext) {
        this.hometext = hometext;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
