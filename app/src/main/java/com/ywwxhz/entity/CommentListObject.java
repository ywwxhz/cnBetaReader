package com.ywwxhz.entity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public class CommentListObject {
    //    private ArrayList<CommentItem> hotlist = new ArrayList<CommentItem>();
    private ArrayList<CommentItem> cmntlist = new ArrayList<CommentItem>();
    private HashMap<String, CommentItem> cmntstore = new HashMap<String, CommentItem>();
    private int comment_num;
    private String token;
    private int page;

//    public ArrayList<CommentItem> getHotlist() {
//        return hotlist;
//    }
//
//    public void setHotlist(ArrayList<CommentItem> hotlist) {
//        this.hotlist = hotlist;
//    }

    public ArrayList<CommentItem> getCmntlist() {
        return cmntlist;
    }

    public void setCmntlist(ArrayList<CommentItem> cmntlist) {
        this.cmntlist = cmntlist;
    }

    public HashMap<String, CommentItem> getCmntstore() {
        return cmntstore;
    }

    public void setCmntstore(HashMap<String, CommentItem> cmntstore) {
        this.cmntstore = cmntstore;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
