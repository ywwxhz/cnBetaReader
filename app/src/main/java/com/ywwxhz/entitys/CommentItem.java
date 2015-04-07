package com.ywwxhz.entitys;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/2 17:53.
 */
public class CommentItem {
    private int score;
    private String tid;
    private String pid;
    private int sid;
    private int reason;
    private String icon;
    private String date;
    private String name;
    private String comment;
    private String host_name;
    private String refContent;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getRefContent() {
        return refContent;
    }

    public void setRefContent(String refContent) {
        this.refContent = refContent;
    }

    public void copy(CommentItem item) {
        this.score = item.getScore();
        this.reason = item.getReason();
        this.icon = item.getIcon();
        this.date = item.getDate();
        this.name = item.getName();
        this.comment = item.getComment();
        this.host_name = item.getHost_name();
    }

}
