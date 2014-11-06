package com.ywwxhz.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class NewsListObject {

    @Expose
    private List<NewsItem> list = new ArrayList<NewsItem>();
    @Expose
    private String type;
    @Expose
    private Integer page;

    /**
     * @return The list
     */
    public List<NewsItem> getList() {
        return list;
    }

    /**
     * @param list The list
     */
    public void setList(List<NewsItem> list) {
        this.list = list;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }


}
