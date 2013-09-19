/** 窗体尺寸变更. */
function doResize() {
	var $win = $(window), width = $win.width() - 280, height = $win.height() - 65;
    $('#mainContent').width(width).height(height);
    $('#mainFrame').width(width).height(height);
    $('#mainList').height(height);
}
 