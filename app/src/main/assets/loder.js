var Loader = (function(){
	var image = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMTc3LjY1NjI1IiB5PSIyMDAuNyIgc3R5bGU9ImZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZTozMHB0Ij7ngrnlh7vliqDovb3lm77niYc8L3RleHQ+PC9nPjwvc3ZnPg==";
	var error = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMjE4LjQzNzUiIHk9IjIwMC43IiBzdHlsZT0iZmlsbDojQUFBQUFBO2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjMwcHQiPueCueWHu+mHjeivlTwvdGV4dD48L2c+PC9zdmc+";
	var loading = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMjM4LjgyODEyNSIgeT0iMjAwLjciIHN0eWxlPSJmaWxsOiNBQUFBQUE7Zm9udC13ZWlnaHQ6Ym9sZDtmb250LWZhbWlseTpBcmlhbCwgSGVsdmV0aWNhLCBPcGVuIFNhbnMsIHNhbnMtc2VyaWYsIG1vbm9zcGFjZTtmb250LXNpemU6MzBwdCI+5Yqg6L295LitPC90ZXh0PjwvZz48L3N2Zz4=";
	var flash = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMTg3Ljc1IiB5PSIyMDAuNyIgc3R5bGU9ImZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZTozMHB0Ij5GbGFzaCBWaWRlbzwvdGV4dD48L2c+PC9zdmc+";
	var no_support = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMTM2Ljg3NSIgeT0iMjAwLjciIHN0eWxlPSJmaWxsOiNBQUFBQUE7Zm9udC13ZWlnaHQ6Ym9sZDtmb250LWZhbWlseTpBcmlhbCwgSGVsdmV0aWNhLCBPcGVuIFNhbnMsIHNhbnMtc2VyaWYsIG1vbm9zcGFjZTtmb250LXNpemU6MzBwdCI+5bCa5pyq5pSv5oyB6K+l6KeG6aKR5rqQPC90ZXh0PjwvZz48L3N2Zz4=";
	var imageSrcs = [];
	var imgs = [];
	var storageElement = [];
	(function () {
		var aimgs = document.querySelectorAll('#content a>img');
		for(var i=0;i<aimgs.length;i++){
			var img = aimgs[i];
			var a = img.parentNode;
			var p = a.parentNode;
			p.replaceChild(img,a);
		}
		imgs = document.querySelectorAll("#content img");
		imageSrcs = [];
		for (var i = 0; i < imgs.length; i++) {
			var img = imgs[i];
			imageSrcs[i] = img.src;
			img.setAttribute("dest-src", img.src);
			img.setAttribute("pos", i);
			img.removeAttribute("width");
			img.removeAttribute("height");
			img.removeAttribute("style");
			if (config.enableImage) {
				loadImage(img);
			} else {
				img.removeAttribute("src");
			//img.setAttribute("data-src","holder.js/600x300/auto/text:Image")
			img.setAttribute("src", image);
			img.onclick = function() {
				loadImage(this);
			};
		}
	};

	var iframes = document.querySelectorAll('iframe');
	for (var i = 0; i < iframes.length; i++) {
		var iframe = iframes[i];
		fixWidthAndHight(iframe,iframe);
	}
	var videos = document.querySelectorAll('video');
	for (var i = 0; i < videos.length; i++) {
		var video = videos[i];
		video.setAttribute("preload","metadata");
		fixWidthAndHight(video,video);
	}
	var embeds = document.querySelectorAll('embed');
	for (var i = 0; i < embeds.length; i++) {
		var embed = embeds[i];
		if(embed.type=="application/x-shockwave-flash"){
			var flashVideo = new Image();
			flashVideo.setAttribute("src", flash);
			flashVideo.setAttribute("id","video_"+i);
			flashVideo.setAttribute("video-src",embed.src);
			flashVideo.setAttribute("video-params",embed.flashvars);
			if(config.enableFlashToHtml5){
				flashVideo.onclick = function() {
					loadVideo(this);
				};
			}
			embed.parentNode.replaceChild(flashVideo,embed);
		}else{
			console.log("other ");
			embed.height = embed.offsetWidth * 10 / 16;
		}
	}

	var sohuDomain = /.*sohu.com.*/;
	var youkuDomain = /.*youku.com.*/;
	var tudouDomain = /.*tudou.com.*/;
	var QQDomain = /.*qq.com.*/;

	function loadVideo(flashVideo){
		flashVideo.setAttribute("src", loading);
		var video_src = flashVideo.getAttribute("video-src");
		console.log("video id "+flashVideo.id+"  loading src= "+video_src);
		if(sohuDomain.test(video_src)&&handleSohuVideo(video_src,flashVideo)){
			return;
		}
		if(youkuDomain.test(video_src)&&handleyoukuVideo(video_src,flashVideo)){
			return;
		}
		if(tudouDomain.test(video_src)&&handletudouVideo(video_src,flashVideo)){
			return;
		}
		if(QQDomain.test(video_src)&&handleQQVideo(video_src,flashVideo)){
			return;
		}
		flashVideo.setAttribute("src", no_support);
		console.log(video_src);
		window.Interface.showMessage("尚未支持 " + getUrlDomain(video_src)+" 视频源","info");
	}

	function handleSohuVideo(video_src,flashVideo){
		var id = getUrlParamByName(video_src,"id");
		if(id!=""){
			var url = "http://api.tv.sohu.com/v4/video/info/"+id+".json?site=2&api_key=9854b2afa779e1a6bff1962447a09dbd";
			console.log("load sohu video id "+id);
			window.Interface.loadSohuVideo(flashVideo.id,url);
			return true;
		}
		return false;
	}

	function handleyoukuVideo(video_src,flashVideo){
		var staticYouku = /.*static.youku.com.*/;
		var id = "";
		if(staticYouku.test(video_src)){
			id = getUrlParamByName(video_src,"VideoIDS");
		}else{
			id = getUrlPathValue(video_src,"sid");
		}
		if(id!=""){
			var iframe = document.createElement("iframe");
			iframe.src = "http://player.youku.com/embed/"+id;
			fixWidthAndHight(iframe,flashVideo);
			iframe.onload=function(){
				console.log("youku html5 video player loading success");
			}
			flashVideo.parentNode.replaceChild(iframe,flashVideo);
			return true;
		}
		return false;
	}

	function handletudouVideo(video_src,flashVideo){
		var tudou = video_src.match(/.*tudou.*\/v\/(\S+)?\/&.*/);
		if(tudou){
			var iframe = document.createElement("iframe");
			iframe.src = "http://www.tudou.com/programs/view/html5embed.action?code="+tudou[1];
			fixWidthAndHight(iframe,flashVideo);
			iframe.onload=function(){
				console.log("tudou html5 video player loading success");
			}
			flashVideo.parentNode.replaceChild(iframe,flashVideo);
			return true;
		}
		return false;
	}

	function handleQQVideo(video_src,flashVideo){
		var vid = getUrlParamByName(video_src,"vid");
		if(vid!=""){
			var iframe = document.createElement("iframe");
			iframe.src = "http://v.qq.com/iframe/player.html?vid="+vid;+"&amp;width="+flashVideo.offsetWidth+"&amp;height="+flashVideo.offsetWidth * 10 / 16+"&amp;auto=0"
			fixWidthAndHight(iframe,flashVideo);
			iframe.onload=function(){
				console.log("QQ html5 video player loading success");
			}
			flashVideo.parentNode.replaceChild(iframe,flashVideo);
			return true;
		}
		return false;
	}
	function getUrlParamByName(url,name) {
		var reg = new RegExp("(^|&\?)" + name + "=([^&]*)(&|$)", "i");
		var r = url.match(reg);  
		var context = "";  
		if (r != null)  
			context = r[2];  
		reg = null;  
		r = null;  
		return context == null || context == "" || context == "undefined" ? "" : context; 
	}

	function getUrlDomain(url){
		var tmp = url.match(/(\w+):\/\/([^\/:|\/]+)(:\d*)?/)
		if(tmp!=null){
			return tmp[2];
		}else{
			return "unknow";
		}
	}

	function getUrlPathValue(url,name){
		var reg = new RegExp("(^|\/)" + name + "\/([^\/]*)(\/|$)", "i");
		var r = url.match(reg); 
		var context = "";  
		if (r != null)  
			context = r[2];  
		reg = null;  
		r = null;  
		return context == null || context == "" || context == "undefined" ? "" : context; 
	}})();

	var _VideoCallBack = function (viewid, src,video_img) {
		var flashVideo = document.getElementById(viewid);
		if (flashVideo) {
			var video = document.createElement("video");
			video.src = src;
			video.poster = video_img;
			video.setAttribute("preload","metadata");
			video.controls = "controls";
			fixWidthAndHight(video, flashVideo);
			flashVideo.parentNode.replaceChild(video, flashVideo);
		} else {
			console.log("Illagel viewid");
		}
	}

	function setWidthHeight(target,width){
		if(width > 600){
           	width = 600;
        }
        target.width = width;
        target.height = width * 10 / 16 ;
        //target.style.maxWidth = width + "px";
        //target.style.maxHeight = width * 10 / 16 + "px";
	}

	function fixWidthAndHight(target, source) {
		storageElement.push(target);
		setWidthHeight(target,source.offsetWidth);
	}


	var _showAllImage =  function () {
		for (var i = 0; i < imgs.length; i++) {
			loadImage(imgs[i]);
		};
	}

	function loadImage(img) {
		if(img.getAttribute("status")!="ok"){
			var image = new Image();
			img.setAttribute("status", "loading");
			img.setAttribute("src", loading);
			image.src = img.getAttribute("dest-src");
			image.onload = function () {
				img.setAttribute("status", "ok");
				img.setAttribute("src", img.getAttribute("dest-src"));
				img.onclick = function () {
					openImage(this);
				};
			}
			image.onerror = function () {
				img.setAttribute("status", "error");
				img.setAttribute("src", error);
				img.onclick = function () {
					loadImage(this);
				};
			};
		}
	}

	function openImage(obj) {
		console.log(obj.getAttribute("pos"));
		window.Interface.showImage(obj.getAttribute("pos"), imageSrcs);
		return false;
	}

	var _setNight = function setNight(isNight) {
		if (isNight) {
			document.body.style.backgroundColor = "#202733";
			document.body.style.color = "#9bafcb";
			document.getElementById('introduce').style.backgroundColor = "#262f3d";
			document.getElementById('introduce').style.color = "#616d80";
			var blockquotes = document.querySelectorAll('blockquote');
			for (var i = blockquotes.length - 1; i >= 0; i--) {
				blockquotes[i].style.backgroundColor = "#262f3d";
				blockquotes[i].style.color = "#616d80";
			};
		} else {
			document.body.style.backgroundColor = "#FFF";
			document.body.style.color = "#000";
			document.getElementById('introduce').style.backgroundColor = "#F1F1F1";
			document.getElementById('introduce').style.color = "#444";
			var blockquotes = document.querySelectorAll('blockquote');
			for (var i = blockquotes.length - 1; i >= 0; i--) {
				blockquotes[i].style.backgroundColor = "#F1F1F1";
				blockquotes[i].style.color = "#444";
			};
		}
	}

	var _updateWidth = function(){
		for(var i=0;i<storageElement.length;i++){
			setWidthHeight(storageElement[i],document.getElementById("content").offsetWidth);
		}
	}

	return {
		setNight:function(isNight){
			_setNight(isNight);
		},
		VideoCallBack:function (viewid, src,video_img){
			_VideoCallBack(viewid, src,video_img);
		},
		showAllImage:function (){
			_showAllImage();
		},
		updateWidth:function (){
			setTimeout(_updateWidth,500);
		}
	}
})();
