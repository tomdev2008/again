function stringFilter(s) { 
	var pattern = new RegExp("[`%@#$^&=|\\[\\]<>/@#￥……&——|]") 
	var rs = ""; 
	for (var i = 0; i < s.length; i++) { 
		rs = rs+s.substr(i, 1).replace(pattern, ''); 
	}
	return rs; 
} 
String.prototype.trim = function() {
  return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
}