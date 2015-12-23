package com.ywwxhz.data.impl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ywwxhz.MyApplication;
import com.ywwxhz.data.BaseDataProvider;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.Toolkit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;

import cz.msebera.android.httpclient.Header;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/31 11:13.
 */
public class NewsDetailProvider extends BaseDataProvider<String> {

    private TextHttpResponseHandler handler =  new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            if(callback!=null) {
                callback.onLoadStart();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if(callback!=null) {
                callback.onLoadFailure();
            }
            if(MyApplication.getInstance().getDebug()){
                Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            if(callback!=null){
                callback.onLoadSuccess(responseString);
            }
        }
    };

    public NewsDetailProvider(Activity activity) {
        super(activity);
    }

    @Override
    public void loadData(boolean startup) {

    }

    public void loadNewsAsync(String sid){
        NetKit.getInstance().getNewsBySid(sid,handler);
    }

    public static boolean handleResponceString(NewsItem item,String resp,boolean shouldCache){
        return handleResponceString(item, resp,shouldCache,false);
    }

    public static boolean handleResponceString(NewsItem item,String resp,boolean shouldCache,boolean cacheImage){
        Document doc = Jsoup.parse(resp);
        Elements newsHeadlines = doc.select(".body");
        item.setTitle(newsHeadlines.select("#news_title").html().replaceAll("<.*?>", ""));
        item.setFrom(newsHeadlines.select(".where").html());
        item.setInputtime(newsHeadlines.select(".date").html());
        Elements introduce = newsHeadlines.select(".introduction");
        introduce.select("div").remove();
        item.setHometext(introduce.html());
        Elements content = newsHeadlines.select(".content");
        content.select(".tigerstock").remove();
        Elements scripts = content.select("script");
        for (int i=0;i<scripts.size();i++){
            Element script = scripts.get(i);
            Element SiblingScript = script.nextElementSibling();
            String _script;
            if(SiblingScript!=null&&SiblingScript.tag()==Tag.valueOf("script")){
                i++;
                _script = script.toString().replaceAll(",?\"?(width|height)\"?:?\"(.*)?\"","");
                _script += SiblingScript.toString();
                _script = _script.replaceAll("\"|'","'");
                SiblingScript.remove();
            }else{
                _script = script.toString().replaceAll(",?\"(width|height)\":\"\\d+\"","").replaceAll("\"|'","'");
            }
            Element element = new Element(Tag.valueOf("iframe"),"");
            element.attr("contentScript",_script);
            element.attr("ignoreHolder","true");
            element.attr("style","width:100%");
            element.attr("allowfullscreen ","true");
            element.attr("onload","VideoTool.onloadIframeVideo(this)");
            script.replaceWith(element);
        }
        if(cacheImage){
            Elements images = content.select("img");
            for(Element image:images){
                Bitmap img = ImageLoader.getInstance().loadImageSync(image.attr("src"), MyApplication.getDefaultDisplayOption());
                if(img!=null) {
                    img.recycle();
                }
            }
        }

        item.setContent(content.html());
        Matcher snMatcher = Configure.SN_PATTERN.matcher(resp);
        if (snMatcher.find())
            item.setSN(snMatcher.group(1));
        if(item.getContent()!=null&&item.getContent().length()>0){
            if(shouldCache) {
                FileCacheKit.getInstance().put(item.getSid() + "", Toolkit.getGson().toJson(item));
            }
            return true;
        }else{
            return false;
        }
    }
}
