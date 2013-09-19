var gridUtils = {};
/**
不选择表格中一行的选择~
**/
gridUtils.unSelectRow = function (gridId,index) {
	setTimeout(function() {
		$("#"+gridId).datagrid("unselectRow",index);
	},200);
}
/**
改变easyUI中默认的翻页bar中的应为提示为中文提示
**/
gridUtils.changeTableBar = function (gridId) {
	var q = $('#'+gridId).datagrid('getPager');  
	$(q).pagination({
	    pageSize: 50,//每页显示的记录条数，默认为50  
	    pageList: [10,20,30,50,100],//可以设置每页记录条数的列表  
	    beforePageText: '第',//页数文本框前显示的汉字  
	    afterPageText: '页    共 {pages} 页',  
	    displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录'
	}); 
}
/**
图片加载完成后，resize整个表格
**/
gridUtils.resizeGrid = function(gridId, count) {
	if (!count) {
		var count = $("#" + gridId).datagrid("getRows").length
		$("#" + gridId + "-wapper img").load(function() {
			count--;
			if(count==0) {
				$("#" + gridId).datagrid("resize");
			}
		});
	} else {
		$("#" + gridId + "-wapper img").load(function() {
			count--;
			if(count==0) {
				$("#" + gridId).datagrid("resize");
			}
		});
	}
}
/**
从grid中获取选择的行ID，以逗号分开
**/
gridUtils.getIdsFromGrid = function (gridId) {
	var rows = $("#" + gridId).datagrid("getSelections");
	if(rows.length == 0) {
		alert("至少选择一条数据");
		return;
	}
	var ids = [];
	for (var i=0;i<rows.length;i++) {
		ids.push(rows[i]._id);
	}
	return ids.join(',');
}
gridUtils.getIdFromGrid = function (gridId) {
	var rows = $("#" + gridId).datagrid("getSelections");
	if (rows.length != 1) {
		alert("只能选择一条数据");
		return;
	}
	return $("#" + gridId).datagrid("getSelected")._id;
}
gridUtils.getPropertyFromGrid = function (gridId, property) {
	var rows = $("#" + gridId).datagrid("getSelections");
	if (rows.length != 1) {
		alert("只能选择一条数据");
		return;
	}
	return $("#" + gridId).datagrid("getSelected")[property];
}
gridUtils.getRowsFromGrid = function (gridId) {
	var rows = $("#" + gridId).datagrid("getSelections");
	return rows;
}
/**
获得一个字符串字节长度
**/
gridUtils.getByteLen = function (val) {
	var len = 0; 
	for (var i = 0; i < val.length; i++) { 
		if (val[i].match(/[^\x00-\xff]/ig) != null) { //全角 
			len += 2;
		}
		else { 
			len += 1;
		}
	}
	return len; 
}
/**
 * 生成一个dategrid表格.
 * @param data: 表格里面自定义的属性,如果没有这个属性就用默认的
 * 一般要传的参数有：url,columns,toolbar,onLoadSuccess等
 */
gridUtils.createGridOption = function (data) {
	var obj = {};
	obj.width = 900;
	obj.fitColumns = true;
	obj.loadMsg = "加载中";
	obj.pagination = true;
	obj.pageNumber = 1;
	obj.pageSize = 50;
	obj.pageList = [10,20,30,50,100,200,1000,2000]
	obj.sortName = "_id";
	obj.sortOrder = "desc";
	obj.remoteSort = true;
	for (var o in data) {
		obj[o] = data[o];
	}
	return obj;
}
/**
 * 扩展数组方法。 检测一个元素是否已存在这个数组中.
 */
Array.prototype.indexOf = function (val) {
	for (var i = 0; i < this.length; i++) {
		if (this[i] == val) {
			return i;
		}
	}
	return -1;
};
/**
 * 扩展数组方法。 根据一个元素的内容，从这个数组里面删除该元素.
 */
Array.prototype.remove = function (val) {
	var index = this.indexOf(val);
	if (index > -1) {
		this.splice(index, 1);
	}
};
/**
 * 扩展数组方法。像一个数组中不重复的加入元素.
 */
Array.prototype.add = function(value) {
	for (var i=0;i<this.length;i++) {
		if (value == this[i]){
			return;
		}
	}
	this.push(value);
}