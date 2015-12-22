var BaseTool = (function(){
	var storageElement = [];
	var _loadingImg= "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MDAiIGhlaWdodD0iMzc1IiBmaWxsPSIjNzc3Ij48cmVjdCB4PSIyNzIuNSIgeT0iMTY3LjUiIHdpZHRoPSI2IiBoZWlnaHQ9IjQwIiByeD0iMiI+PGFuaW1hdGUgYXR0cmlidXRlTmFtZT0iaGVpZ2h0IiBiZWdpbj0iMC41cyIgZHVyPSIwLjhzIiB2YWx1ZXM9IjQwOzM1OzMwOzI1OzIwOzE1OzEwOzU7NDU7NDAiIGNhbGNNb2RlPSJsaW5lYXIiIHJlcGVhdENvdW50PSJpbmRlZmluaXRlIiAvPjxhbmltYXRlIGF0dHJpYnV0ZU5hbWU9InkiIGJlZ2luPSIwLjVzIiBkdXI9IjAuOHMiIHZhbHVlcz0iMTY3LjU7MTcwOzE3Mi41OzE3NTsxNzcuNTsxODA7MTgyLjU7MTg1OzE2NTsxNjcuNSIgY2FsY01vZGU9ImxpbmVhciIgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiIC8+PC9yZWN0PjxyZWN0IHg9IjI4Mi41IiB5PSIxNjcuNSIgd2lkdGg9IjYiIGhlaWdodD0iNDAiIHJ4PSIyIj48YW5pbWF0ZSBhdHRyaWJ1dGVOYW1lPSJoZWlnaHQiIGJlZ2luPSIwLjI1cyIgZHVyPSIwLjhzIiB2YWx1ZXM9IjQwOzM1OzMwOzI1OzIwOzE1OzEwOzU7NDU7NDAiIGNhbGNNb2RlPSJsaW5lYXIiIHJlcGVhdENvdW50PSJpbmRlZmluaXRlIiAvPjxhbmltYXRlIGF0dHJpYnV0ZU5hbWU9InkiIGJlZ2luPSIwLjI1cyIgZHVyPSIwLjhzIiB2YWx1ZXM9IjE2Ny41OzE3MDsxNzIuNTsxNzU7MTc3LjU7MTgwOzE4Mi41OzE4NTsxNjU7MTY3LjUiIGNhbGNNb2RlPSJsaW5lYXIiIHJlcGVhdENvdW50PSJpbmRlZmluaXRlIiAvPjwvcmVjdD48cmVjdCB4PSIyOTIuNSIgeT0iMTY3LjUiIHdpZHRoPSI2IiBoZWlnaHQ9IjQwIiByeD0iMiI+PGFuaW1hdGUgYXR0cmlidXRlTmFtZT0iaGVpZ2h0IiBiZWdpbj0iMHMiIGR1cj0iMC44cyIgdmFsdWVzPSI0MDszNTszMDsyNTsyMDsxNTsxMDs1OzQ1OzQwIiBjYWxjTW9kZT0ibGluZWFyIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSIgLz48YW5pbWF0ZSBhdHRyaWJ1dGVOYW1lPSJ5IiBiZWdpbj0iMHMiIGR1cj0iMC44cyIgdmFsdWVzPSIxNjcuNTsxNzA7MTcyLjU7MTc1OzE3Ny41OzE4MDsxODIuNTsxODU7MTY1OzE2Ny41IiBjYWxjTW9kZT0ibGluZWFyIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSIgLz48L3JlY3Q+PHJlY3QgeD0iMzAyLjUiIHk9IjE2Ny41IiB3aWR0aD0iNiIgaGVpZ2h0PSI0MCIgcng9IjIiPjxhbmltYXRlIGF0dHJpYnV0ZU5hbWU9ImhlaWdodCIgYmVnaW49IjAuMjVzIiBkdXI9IjAuOHMiIHZhbHVlcz0iNDA7MzU7MzA7MjU7MjA7MTU7MTA7NTs0NTs0MCIgY2FsY01vZGU9ImxpbmVhciIgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiIC8+PGFuaW1hdGUgYXR0cmlidXRlTmFtZT0ieSIgYmVnaW49IjAuMjVzIiBkdXI9IjAuOHMiIHZhbHVlcz0iMTY3LjU7MTcwOzE3Mi41OzE3NTsxNzcuNTsxODA7MTgyLjU7MTg1OzE2NTsxNjcuNSIgY2FsY01vZGU9ImxpbmVhciIgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiIC8+PC9yZWN0PjxyZWN0IHg9IjMxMi41IiB5PSIxNjcuNSIgd2lkdGg9IjYiIGhlaWdodD0iNDAiIHJ4PSIyIj48YW5pbWF0ZSBhdHRyaWJ1dGVOYW1lPSJoZWlnaHQiIGJlZ2luPSIwLjVzIiBkdXI9IjAuOHMiIHZhbHVlcz0iNDA7MzU7MzA7MjU7MjA7MTU7MTA7NTs0NTs0MCIgY2FsY01vZGU9ImxpbmVhciIgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiIC8+PGFuaW1hdGUgYXR0cmlidXRlTmFtZT0ieSIgYmVnaW49IjAuNXMiIGR1cj0iMC44cyIgdmFsdWVzPSIxNjcuNTsxNzA7MTcyLjU7MTc1OzE3Ny41OzE4MDsxODIuNTsxODU7MTY1OzE2Ny41IiBjYWxjTW9kZT0ibGluZWFyIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSIgLz48L3JlY3Q+PC9zdmc+";

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
        //target.style.maxWidth = width + "px";
        //target.style.maxHeight = width * 10 / 16 + "px";
	}
	
	var _updateWidth = function(){
		for(var i=0;i<storageElement.length;i++){
			setWidthHeight(storageElement[i],document.getElementById("content").offsetWidth);
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
		loadingImg:_loadingImg

	}
})();