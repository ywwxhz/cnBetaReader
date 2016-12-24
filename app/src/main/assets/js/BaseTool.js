var BaseTool = (function(){
	var storageElement = [];
	var _loadingImg= "file:///android_asset/svg/loading.svg";
	var _loadingImgStatic= "file:///android_asset/svg/loading_static.svg";

	function _fixWidthAndHight(target, source) {
		storageElement.push(target);
		setWidthHeight(target,source.offsetWidth);
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
	
	function setWidthHeight(target,width){
		if(width > 600){
           		width = 600;
        }
        target.width = width;
        target.height = width * 10 / 16 ;
        target.style.maxWidth = width + "px";
        target.style.maxHeight = width * 10 / 16 + "px";
	}
	
	var _updateWidth = function(){
		for(var i=0;i<storageElement.length;i++){
			setWidthHeight(storageElement[i],document.getElementById("content").offsetWidth);
		}
	}

    function hasClass( elements,cName ){
        return !!elements.className.match( new RegExp( "(\\s|^)" + cName + "(\\s|$)") ); // ( \\s|^ ) 判断前面是否有空格 （\\s | $ ）判断后面是否有空格 两个感叹号为转换为布尔值 以方便做判断
    };

	function addClass( elements,cName ){
        if( !hasClass( elements,cName ) ){
            elements.className += " " + cName;
        };
    };

    function removeClass( elements,cName ){
        if( hasClass( elements,cName ) ){
            elements.className = elements.className.replace( new RegExp( "(\\s|^)" + cName + "(\\s|$)" )," " ); // replace方法是替换
        };
    };

	var _traggleBlock = function(show){
	    var blockeds = document.querySelectorAll(".blocked");
	    for(var i =0;i<blockeds.length;i++){
	        var blocked = blockeds[i];
            if(show){
                addClass(blocked,"showBlocked");
            }else{
                removeClass(blocked,"showBlocked");
            }
	    }
	}



    return {
		fixWidthAndHight:function(target, source){
			_fixWidthAndHight(target, source)
		},
		setNight:function(isNight){
			_setNight(isNight);
		},
		updateWidth:function (){
			setTimeout(_updateWidth,500);
		},
		traggleBlock:function(show){
		    _traggleBlock(show);
		},
		loadingImg:function(){
		    if(config.staticLoading){
		        return _loadingImgStatic;
		    }else{
		        return _loadingImg;
		    }
		}

	}
})();