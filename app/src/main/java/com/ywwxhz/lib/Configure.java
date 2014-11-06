package com.ywwxhz.lib;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class Configure {

    public static final String BASE_URL = "http://www.cnbeta.com";
    private static final String NEWS_LIST_URL = BASE_URL + "/more?type=%s&page=%s&sid=%s";
    private static final String ARTICLE_URL = BASE_URL + "/articles/%s.htm";
    private static final String COMMENT_URL = BASE_URL + "/cmt?op=1,%s,%s";
    public static final String COMMENT_VIEW = BASE_URL +"/comment";
    public static final Pattern STANDRA_PATTERN = Pattern.compile("cnBeta\\.COM_中文业界资讯站");
    public static final Pattern SN_PATTERN = Pattern.compile("SN:\"(.{5})\"");
    public static final Pattern TITLE_PATTERN = Pattern.compile("<h2 id=\"news_title\">(.*?)</h2>");
    public static final Pattern CONTENT_PATTERN = Pattern.compile("<div class=\"content\">(.*?)</div>", Pattern.DOTALL);
    public static final Pattern FROM_PATTERN = Pattern.compile("<span class=\"where\"> ?稿源：(<a .*?>)?(.*?)(</a>)?</span>");
    public static final Pattern ICON_PATTERN = Pattern.compile("<a href=\"/topics/\\d+.htm\" target=\"_blank\"><img title=\".*?\" src=\"(.*?)\" /></a>");

    public static String buildNewsListUrl(String type, String page, String sid) {
        return String.format(Locale.CHINA, NEWS_LIST_URL, type, page, sid);
    }

    public static String buildArticleUrl(String sid) {
        return String.format(Locale.CHINA, ARTICLE_URL, sid);
    }

    public static String buildCommentUrl(String sid, String SN) {
        return String.format(Locale.CHINA, COMMENT_URL, sid, SN);
    }
}
