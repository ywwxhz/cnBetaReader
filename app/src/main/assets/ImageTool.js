var ImageTool = (function(){
	var image = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMTc3LjY1NjI1IiB5PSIyMDAuNyIgc3R5bGU9ImZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZTozMHB0Ij7ngrnlh7vliqDovb3lm77niYc8L3RleHQ+PC9nPjwvc3ZnPg==";
	var error = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiB2aWV3Qm94PSIwIDAgNjAwIDM3NSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+PGRlZnMvPjxyZWN0IHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjRUVFRUVFIi8+PGc+PHRleHQgeD0iMjE4LjQzNzUiIHk9IjIwMC43IiBzdHlsZT0iZmlsbDojQUFBQUFBO2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjMwcHQiPueCueWHu+mHjeivlTwvdGV4dD48L2c+PC9zdmc+";
	var loading = "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIj8+PHN2ZyB3aWR0aD0iNjAwIiBoZWlnaHQ9IjM3NSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiBmaWxsPSIjNzc3Ij48cmVjdCB4PSIyNzIuNSIgeT0iMTY3LjUiIHdpZHRoPSI2IiBoZWlnaHQ9IjQwIiByeD0iMiI+PGFuaW1hdGUgYXR0cmlidXRlTmFtZT0iaGVpZ2h0IiBiZWdpbj0iMC41cyIgZHVyPSIxcyIgdmFsdWVzPSI0MDszNTszMDsyNTsyMDsxNTsxMDs1OzQ1OzQwIiBjYWxjTW9kZT0ibGluZWFyIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSIgLz48YW5pbWF0ZSBhdHRyaWJ1dGVOYW1lPSJ5IiBiZWdpbj0iMC41cyIgZHVyPSIxcyIgdmFsdWVzPSIxNjcuNTsxNzA7MTcyLjU7MTc1OzE3Ny41OzE4MDsxODIuNTsxODU7MTY1OzE2Ny41IiBjYWxjTW9kZT0ibGluZWFyIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSIgLz48L3JlY3Q+PHJlY3QgeD0iMjgyLjUiIHk9IjE2Ny41IiB3aWR0aD0iNiIgaGVpZ2h0PSI0MCIgcng9IjIiPjxhbmltYXRlIGF0dHJpYnV0ZU5hbWU9ImhlaWdodCIgYmVnaW49IjAuMjVzIiBkdXI9IjFzIiB2YWx1ZXM9IjQwOzM1OzMwOzI1OzIwOzE1OzEwOzU7NDU7NDAiIGNhbGNNb2RlPSJsaW5lYXIiIHJlcGVhdENvdW50PSJpbmRlZmluaXRlIiAvPjxhbmltYXRlIGF0dHJpYnV0ZU5hbWU9InkiIGJlZ2luPSIwLjI1cyIgZHVyPSIxcyIgdmFsdWVzPSIxNjcuNTsxNzA7MTcyLjU7MTc1OzE3Ny41OzE4MDsxODIuNTsxODU7MTY1OzE2Ny41IiBjYWxjTW9kZT0ibGluZWFyIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSIgLz48L3JlY3Q+PHJlY3QgeD0iMjkyLjUiIHdpZHRoPSI2IiBoZWlnaHQ9IjUiIHJ4PSIyIj48YW5pbWF0ZSBhdHRyaWJ1dGVOYW1lPSJoZWlnaHQiIGJlZ2luPSIwcyIgZHVyPSIxcyIgdmFsdWVzPSI0MDszNTszMDsyNTsyMDsxNTsxMDs1OzQ1OzQwIiBjYWxjTW9kZT0ibGluZWFyIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSIgLz48YW5pbWF0ZSBhdHRyaWJ1dGVOYW1lPSJ5IiBiZWdpbj0iMHMiIGR1cj0iMXMiIHZhbHVlcz0iMTY3LjU7MTcwOzE3Mi41OzE3NTsxNzcuNTsxODA7MTgyLjU7MTg1OzE2NTsxNjcuNSIgY2FsY01vZGU9ImxpbmVhciIgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiIC8+PC9yZWN0PjxyZWN0IHg9IjMwMi41IiB5PSIxNjcuNSIgd2lkdGg9IjYiIGhlaWdodD0iNDAiIHJ4PSIyIj48YW5pbWF0ZSBhdHRyaWJ1dGVOYW1lPSJoZWlnaHQiIGJlZ2luPSIwLjI1cyIgZHVyPSIxcyIgdmFsdWVzPSI0MDszNTszMDsyNTsyMDsxNTsxMDs1OzQ1OzQwIiBjYWxjTW9kZT0ibGluZWFyIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSIgLz48YW5pbWF0ZSBhdHRyaWJ1dGVOYW1lPSJ5IiBiZWdpbj0iMC4yNXMiIGR1cj0iMXMiIHZhbHVlcz0iMTY3LjU7MTcwOzE3Mi41OzE3NTsxNzcuNTsxODA7MTgyLjU7MTg1OzE2NTsxNjcuNSIgY2FsY01vZGU9ImxpbmVhciIgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiIC8+PC9yZWN0PjxyZWN0IHg9IjMxMi41IiB5PSIxNjcuNSIgd2lkdGg9IjYiIGhlaWdodD0iNDAiIHJ4PSIyIj48YW5pbWF0ZSBhdHRyaWJ1dGVOYW1lPSJoZWlnaHQiIGJlZ2luPSIwLjVzIiBkdXI9IjFzIiB2YWx1ZXM9IjQwOzM1OzMwOzI1OzIwOzE1OzEwOzU7NDU7NDAiIGNhbGNNb2RlPSJsaW5lYXIiIHJlcGVhdENvdW50PSJpbmRlZmluaXRlIiAvPjxhbmltYXRlIGF0dHJpYnV0ZU5hbWU9InkiIGJlZ2luPSIwLjVzIiBkdXI9IjFzIiB2YWx1ZXM9IjE2Ny41OzE3MDsxNzIuNTsxNzU7MTc3LjU7MTgwOzE4Mi41OzE4NTsxNjU7MTY3LjUiIGNhbGNNb2RlPSJsaW5lYXIiIHJlcGVhdENvdW50PSJpbmRlZmluaXRlIiAvPjwvcmVjdD48L3N2Zz4=";
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
					continer.id="image_continer_"+i;
					if(subimgs.length>1){
						for(var j=0;j<subimgs.length;j++){
							var subimg = subimgs[j];
							subimg.setAttribute("ignore","true");
							subimg.remove();
							continer.appendChild(subimg);
						}
						p.replaceChild(continer,a);
					}else{
						p.replaceChild(img,a);
					}

				}
			}
		}
	}
	
	function replaceImage(){
		imgs = document.querySelectorAll("#content img");
		imageSrcs = [];
		for (var i = 0; i < imgs.length; i++) {
			var img = imgs[i];
			if(img.getAttribute("ignoreHolder")==undefined){
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
			}
		};
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
	
	return {
		showAllImage:function (){
			_showAllImage();
		},
		process:function(){
			replaceImageWithA();
			replaceImage();
		}
	}
})();