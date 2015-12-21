var VideoTool = (function(){
	var no_support = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMTM2Ljg3NSIgeT0iMjAwLjciIHN0eWxlPSJmaWxsOiNBQUFBQUE7Zm9udC13ZWlnaHQ6Ym9sZDtmb250LWZhbWlseTpBcmlhbCwgSGVsdmV0aWNhLCBPcGVuIFNhbnMsIHNhbnMtc2VyaWYsIG1vbm9zcGFjZTtmb250LXNpemU6MzBwdCI+5bCa5pyq5pSv5oyB6K+l6KeG6aKR5rqQPC90ZXh0PjwvZz48L3N2Zz4=";
    var video_img = "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iNjAwIiBoZWlnaHQ9IjM3NSIgdmlld0JveD0iMCAwIDYwMCAzNzUiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTFiNTYyNWU0MyB0ZXh0IHsgZmlsbDojQUFBQUFBO2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjMwcHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MWI1NjI1ZTQzIj48cmVjdCB3aWR0aD0iNjAwIiBoZWlnaHQ9IjM3NSIgZmlsbD0iI0VFRUVFRSIvPjxnPjx0ZXh0IHg9IjI0NS41NDY4NzUiIHk9IjIwMC43Ij5WaWRlbzwvdGV4dD48L2c+PC9nPjwvc3ZnPg==";
    	
    function _onloadIframeVideo(iframe){
    	if(config.enableImage){
    		try{
    			writeFrameContent(iframe);
    		}catch(e){
    			console.log(e);
    		}
    	}else{
    		(function ( _iframe ){
    			var iframeVideo = new Image();
		    	iframeVideo.setAttribute("src", video_img);
			    iframeVideo.setAttribute("ignoreHolder", true);
			    iframeVideo.onclick = function() {
				console.log(_iframe.id)
				this.parentNode.replaceChild(_iframe,this);
					writeFrameContent(_iframe);
				};
				_iframe.parentNode.replaceChild(iframeVideo,_iframe);
    		})(iframe);
    	}
    	iframe.removeAttribute("onload");
    }
    function writeFrameContent(iframe){
    	iframe.contentWindow.document.write('<style>*{margin:0;padding:0}</style>');
    	iframe.contentWindow.document.write(iframe.getAttribute("contentScript"));
    	BaseTool.fixWidthAndHight(iframe, document.getElementById("content"));
    }
    	
	function _process(){
		var videos = document.querySelectorAll('video');
		for (var i = 0; i < videos.length; i++) {
			var video = videos[i];
			video.setAttribute("preload","metadata");
			BaseTool.fixWidthAndHight(video,video);
		}
		var embeds = document.querySelectorAll('embed');
		for (var i = 0; i < embeds.length; i++) {
			var embed = embeds[i];
			if(embed.type=="application/x-shockwave-flash"){
				var flashVideo = new Image();
				flashVideo.setAttribute("src", video_img);
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
	}
    	
    	

	var sohuDomain = /.*sohu.com.*/;
	var youkuDomain = /.*youku.com.*/;
	var tudouDomain = /.*tudou.com.*/;
	var QQDomain = /.*qq.com.*/;

	function loadVideo(flashVideo){
		flashVideo.setAttribute("src", BaseTool.loadingImg);
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
			iframe.setAttribute("allowfullscreen","true");
			BaseTool.fixWidthAndHight(iframe,flashVideo);
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
			iframe.setAttribute("allowfullscreen","true");
			BaseTool.fixWidthAndHight(iframe,flashVideo);
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
			iframe.setAttribute("allowfullscreen","true");
			BaseTool.fixWidthAndHight(iframe,flashVideo);
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
	}

	var _VideoCallBack = function (viewid, src,video_img) {
		var flashVideo = document.getElementById(viewid);
		if (flashVideo) {
			var video = document.createElement("video");
			video.src = src;
			video.poster = video_img;
			video.setAttribute("preload","metadata");
			video.controls = "controls";
			BaseTool.fixWidthAndHight(video, flashVideo);
			flashVideo.parentNode.replaceChild(video, flashVideo);
		} else {
			console.log("Illagel viewid");
		}
	}
    	
    return {
		onloadIframeVideo:function(iframe){
			_onloadIframeVideo(iframe);
		},
		process:function(){
			_process();
		},
		VideoCallBack:function (viewid, src,video_img){
			_VideoCallBack(viewid, src,video_img);
		}
	}
})();