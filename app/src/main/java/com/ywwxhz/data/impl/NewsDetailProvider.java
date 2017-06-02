package com.ywwxhz.data.impl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ywwxhz.MyApplication;
import com.ywwxhz.data.BaseDataProvider;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.lib.BlockList;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.handler.BaseCallback;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.Toolkit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;

import okhttp3.Response;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/31 11:13.
 */
public class NewsDetailProvider extends BaseDataProvider<NewsItem> {

	private NewsItem mNewsItem;

	private BaseCallback<NewsItem> handler = new BaseCallback<NewsItem>() {

		@Override
		public void onBefore(BaseRequest request) {
			if (callback != null) {
				callback.onLoadStart();
			}
		}

		@Override
		public NewsItem convertSuccess(Response response) throws Exception {
			String resp = response.body().string();
			if (Configure.STANDRA_PATTERN.matcher(resp).find()) {
				handleResponceString(mNewsItem, resp, true);
			} else {
				OkGo.getInstance().getDelivery().post(new Runnable() {
					@Override
					public void run() {
						callback.onLoadFailure();
					}
				});
			}
			return mNewsItem;
		}

		@Override
		protected void onError(int httpCode, Response response, Exception cause) {
			if (callback != null) {
				callback.onLoadFailure();
			}
			if (cause != null) {
				if (MyApplication.getInstance().getDebug()) {
					Toast.makeText(getActivity(), cause.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		protected void onResponse(NewsItem newsItem) {
			if (callback != null) {
				callback.onLoadSuccess(newsItem);
			}
		}
	};

	public NewsDetailProvider(Activity activity) {
		super(activity);
	}

	public static boolean handleResponceString(NewsItem item, String resp, boolean shouldCache) {
		return handleResponceString(item, resp, shouldCache, false);
	}

	public static boolean handleResponceString(NewsItem item, String resp, boolean shouldCache, boolean cacheImage) {
		Document doc = Jsoup.parse(resp);
		Elements newsHeadlines = doc.select(".cnbeta-article");
		item.setTitle(newsHeadlines.select(".title h1").html().replaceAll("<.*?>", ""));
		item.setFrom(newsHeadlines.select(".source").html());
		item.setInputtime(newsHeadlines.select(".meta span").get(0).html());
		Elements introduce = newsHeadlines.select(".article-summary");
		Elements thumb = introduce.select("img");
		if (thumb.size() > 0) {
			item.setThumb(thumb.get(0).attributes().get("src"));
		}
		introduce.select("div").remove();
		item.setHometext(introduce.html());
		Elements content = newsHeadlines.select(".article-content");
		Elements scripts = content.select("script");
		for (int i = 0; i < scripts.size(); i++) {
			Element script = scripts.get(i);
			Element SiblingScript = script.nextElementSibling();
			String _script;
			if (SiblingScript != null && SiblingScript.tag() == Tag.valueOf("script")) {
				i++;
				_script = script.toString().replaceAll(",?\"?(width|height)\"?:?\"(.*)?\"", "");
				_script += SiblingScript.toString();
				_script = _script.replaceAll("\"|'", "'");
				SiblingScript.remove();
			} else {
				_script = script.toString().replaceAll(",?\"(width|height)\":\"\\d+\"", "").replaceAll("\"|'", "'");
			}
			Element element = new Element(Tag.valueOf("iframe"), "");
			element.attr("contentScript", _script);
			element.attr("ignoreHolder", "true");
			element.attr("style", "width:100%");
			element.attr("allowfullscreen ", "true");
			element.attr("onload", "VideoTool.onloadIframeVideo(this)");
			script.replaceWith(element);
		}
		Elements imagea = content.select("a>img");
		for (Element image : imagea) {
			if ("".equals(image.attr("ignore"))) {
				Element a = image.parent();
				if (a != null) {
					Element element = new Element(Tag.valueOf("div"), "");
					element.attr("RemoveHandlar", "java");
					Elements children = a.children();
					for (Element subimg : children) {
						handleImage(subimg);
						element.appendChild(subimg);
					}
					a.replaceWith(element);
				}
			}
		}
		Elements images = content.select("img");
		for (Element image : images) {
			if (cacheImage) {
				Bitmap img = ImageLoader.getInstance().loadImageSync(image.attr("src"),
						MyApplication.getDefaultDisplayOption());
				if (img != null) {
					img.recycle();
				}
			}
			if ("".equals(image.attr("ignore"))) {
				handleImage(image);
			}
		}
		if (BlockList.size() != 0) {
			// 屏蔽指定元素
			Elements ps = content.select("p");
			for (Element p : ps) {
				for (String remove : BlockList.getRemoveList()) {
					if (p.toString().contains(remove)) {
						p.addClass("blocked");
						p.attr("tag","ad");
						break;
					}
				}
			}
		}

		item.setContent(content.html());
		Matcher snMatcher = Configure.SN_PATTERN.matcher(resp);
		if (snMatcher.find())
			item.setSN(snMatcher.group(1));
		if (item.getContent() != null && item.getContent().length() > 0) {
			if (shouldCache) {
				FileCacheKit.getInstance().put(item.getSid() + "", Toolkit.getGson().toJson(item));
			}
			return true;
		} else {
			return false;
		}
	}

	private static void handleImage(Element image) {
		image.attr("ignore", "true");
		image.attr("tag","img");
		image.attr("replaceSrcHandlar", "java");
		image.attr("dest-src", image.attr("src"));
		image.attr("src", "file:///android_asset/svg/empty.svg");
		image.removeAttr("width");
		image.removeAttr("height");
		image.removeAttr("style");
		// image.removeAttr("src");
		image.attr("onload", "ImageTool.onLoadImage(this)");
		image.attr("onerror", "ImageTool.onLoadImageError(this)");
	}

	@Override
	public void loadData(boolean startup) {

	}

	public void loadNewsAsync(NewsItem mNewsItem) {
		this.mNewsItem = mNewsItem;
		if(TextUtils.isEmpty(mNewsItem.getUrl_show())){
			NetKit.getNewsBySid(getActivity(), mNewsItem.getSid() + "", handler);
		}else{
			NetKit.getNewsByUrl(getActivity(), mNewsItem.getUrl_show(), handler);
		}
	}
}
