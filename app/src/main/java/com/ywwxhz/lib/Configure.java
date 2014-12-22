package com.ywwxhz.lib;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class Configure {

    public static final String BASE_URL = "http://www.cnbeta.com";
    public static final String NEWS_LIST_URL = BASE_URL + "/more";
    private static final String ARTICLE_URL = BASE_URL + "/articles/%s.htm";
    public static final String COMMENT_URL = BASE_URL + "/cmt";
    public static final String COMMENT_VIEW = BASE_URL +"/comment";
    public static final Pattern STANDRA_PATTERN = Pattern.compile("cnBeta\\.COM_中文业界资讯站");
    public static final Pattern SN_PATTERN = Pattern.compile("SN:\"(.{5})\"");

    public static String buildArticleUrl(String sid) {
        return String.format(Locale.CHINA, ARTICLE_URL, sid);
    }
}
