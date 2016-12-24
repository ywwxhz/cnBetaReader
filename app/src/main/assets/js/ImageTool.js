// 图像加载工具
var ImageTool = (function(){
//默认显示图片
	var image = "file:///android_asset/svg/loadImage.svg";
	//默认加载数据失败图片
	var error = "file:///android_asset/svg/error.svg";
	imageSrcs = [];
	imgs = [];
	// 替换图片资源
	function replaceImage(){
		imgs = document.querySelectorAll("#content img[tag]");
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
	// 显示所有图片
	var _showAllImage =  function () {
		for (var i = 0; i < imgs.length; i++) {
			loadImage(imgs[i]);
		};
	}
    // 加载图片
	function loadImage(img) {
		if(img.getAttribute("status")!="ok"){
			var image = new Image();
			img.setAttribute("status", "loading");
			img.setAttribute("src", BaseTool.loadingImg());
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
	// 加载图片
	function _onLoadImage(img) {
	    img.onload=function(){};
	    if (config.enableImage) {
            loadImage(img);
        } else {
            img.setAttribute("src", image);
            img.onclick = function() {
                loadImage(this);
            };
        }
    }
    // 加载图片失败后的操作
    function _onLoadImageError(img) {
        img.setAttribute("status", "error");
    	img.setAttribute("src", error);
    	img.onclick = function () {
    	    loadImage(this);
    	};
    }

    // 打开图片
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