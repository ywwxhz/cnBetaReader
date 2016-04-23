var Loader = (function(){

	(function () {
		ImageTool.process();
		var iframes = document.querySelectorAll('iframe');
		for (var i = 0; i < iframes.length; i++) {
			var iframe = iframes[i];
			BaseTool.fixWidthAndHight(iframe,iframe);
		}
		VideoTool.process();
	})();
})();
