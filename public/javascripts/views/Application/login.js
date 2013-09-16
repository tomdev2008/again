 (function () {
	$(window).bind('resize', doResize);
	doResize();
})();

 /** 窗体尺寸变更. */
 function doResize() {
	var $win = $(window), width = $win.width() - 18, height = $win.height() - 106;
    $('#loginSpace').width(width).height(height);
}
 