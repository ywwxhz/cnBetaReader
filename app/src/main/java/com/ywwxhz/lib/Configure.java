package com.ywwxhz.lib;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:55.
 */
public class Configure {

    public static final String BASE_URL = "https://www.cnbeta.com";
    public static final String NEWS_LIST_URL = BASE_URL + "/home/more";
    private static final String ARTICLE_URL = BASE_URL + "/articles/%s.htm";
    public static final String COMMENT_URL = "https://hot.cnbeta.com/comment/read";
    public static final Pattern ARTICLE_PATTERN = Pattern.compile("(.*)cnbeta.com(.*)");
    public static final Pattern FAVOR_NEWS_TITLE = Pattern.compile("^(\\[|《|”)?((.)?)");
    public static final Pattern STANDRA_PATTERN = Pattern.compile("cnBeta\\.COM_中文业界资讯站");
    public static final Pattern SN_PATTERN = Pattern.compile("SN:\"(.{5})\"");
    public static final Pattern HOT_COMMENT_PATTERN = Pattern.compile("来自<strong>(.*)</strong>的(.*)对新闻:<a href=\"(.*)\" target=\"_blank\">(.*)</a>的评论");
    public static String buildArticleUrl(String sid) {
        return String.format(Locale.CHINA, ARTICLE_URL, sid);
    }
}
