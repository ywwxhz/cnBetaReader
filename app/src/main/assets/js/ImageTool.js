var ImageTool = (function(){
	var image = "file:///android_asset/svg/loadImage.svg";
	var error = "file:///android_asset/svg/error.svg";
	imageSrcs = [];
	imgs = [];
	function replaceImageWithA(){
		console.log("aaaa");
		var aimgs = document.querySelectorAll('#content a>img');
		for(var i=0;i<aimgs.length;i++){
			var img = aimgs[i];
			if(img.getAttribute("ignore")==undefined){
				var a = img.parentNode;
				var p = a.parentNode;
				if(a!=undefined){
					var subimgs = a.querySelectorAll("img");
					var continer = document.createElement("div");
					continer.setAttribute("RemoveHandlar","javaScript");
					for(var j=0;j<subimgs.length;j++){
						var subimg = subimgs[j];
						subimg.setAttribute("ignore","true");
						handleImage(subimg);
						subimg.remove();
						continer.appendChild(subimg);
					}
					p.replaceChild(continer,a);
				}
			}
		}
	}
	
	function replaceImage(){
		imgs = document.querySelectorAll("#content img");
		imageSrcs = [];
		for (var i = 0; i < imgs.length; i++) {
			var img = imgs[i];
			img.setAttribute("pos", i);
			if(img.getAttribute("ignoreHolder")==undefined){
			    if(img.getAttribute("dest-src")==undefined){
                    imageSrcs[i] = img.src;
                }else{
                    imageSrcs[i] = img.getAttribute("dest-src");
                }
                if(img.getAttribute("ignore")==undefined){
                    handleImage(img);
                }
			}
		};
	}

	function handleImage(img){
	    img.setAttribute("dest-src", img.src);
        img.setAttribute("replaceSrcHandlar", "javaScript");
        img.removeAttribute("src");
        img.removeAttribute("width");
        img.removeAttribute("height");
        img.removeAttribute("style");
        if (config.enableImage) {
            loadImage(img);
        } else {
            //img.setAttribute("data-src","holder.js/600x300/auto/text:Image")
            img.setAttribute("src", image);
            img.onclick = function() {
                loadImage(this);
            };
        }
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
			img.setAttribute("src", BaseTool.loadingImg);
			image.src = img.getAttribute("dest-src");
			image.onload = function () {
				img.setAttribute("status", "ok");
				img.setAttribute("src", img.getAttribute("dest-src"));
				img.onclick = function () {
					openImage(this);
				};
			}
			image.onerror = function () {
			    _onLoadImageError(img);
			};
		}
	}
	function _onLoadImage(img) {
	    img.onload=function(){};
	    if (config.enableImage) {
            loadImage(img);
        } else {
            //img.setAttribute("data-src","holder.js/600x300/auto/text:Image")
            img.setAttribute("src", image);
            img.onclick = function() {
                loadImage(this);
            };
        }
    }
    function _onLoadImageError(img) {
        img.setAttribute("status", "error");
    	img.setAttribute("src", error);
    	img.onclick = function () {
    	    loadImage(this);
    	};
    }

	function openImage(obj) {
		console.log(obj.getAttribute("pos"));
		window.Interface.showImage(obj.getAttribute("pos"), imageSrcs);
		return false;
	}
	
	return {
		showAllImage:function (){
			_showAllImage();
		},
		process:function(){
			replaceImageWithA();
			replaceImage();
		},
		onLoadImage:function(img){
		    _onLoadImage(img);
		},
		onLoadImageError:function(img){
		    _onLoadImageError(img);
		}
	}
})();