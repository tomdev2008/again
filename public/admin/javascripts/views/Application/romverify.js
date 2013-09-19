/**
 * @author: zhangdianbao
 * @since: 2013-01-23
 */
var realDelUrl = "";
$(document).ready(function() {

	var scrollTop = 0;
	$(".searchButton").linkbutton({iconCls:'icon-search'});
	/**
		初始化加载数据
	**/
	$("#awaitSearch").val("");
	$("#acceptSearch").val("");
	$("#denySearch").val("");
	showAwaitGrid();
	showAcceptGrid();
	showDenyGrid();
	/**
		重新刷新grid中的数据
	**/
	function reloadAll() {
		$("#awaitSearch").val("");
		$("#acceptSearch").val("");
		$("#denySearch").val("");
		$("#awaitList").datagrid("reload");
		$("#acceptList").datagrid("reload");
		$("#denyList").datagrid("reload");
	};
	/**
		去掉表格中一行的选择~
	**/
	function unSelectRow(gridId,index) {
		setTimeout(function() {
			$("#"+gridId).datagrid("unselectRow",index);
		},200);
	}
	
	/**
		改变easyUI中默认的翻页bar中的应为提示为中文提示
	**/
	function changeTableBar(val) {
		var q = $('#'+val).datagrid('getPager');  
	    $(q).pagination({
	        pageSize: 20,//每页显示的记录条数，默认为20  
	        pageList: [10,20,30,50,100],//可以设置每页记录条数的列表  
	        beforePageText: '第',//页数文本框前显示的汉字  
	        afterPageText: '页    共 {pages} 页',  
	        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录'
	    }); 
	}
	
	/**
		图片加载完成后，resize整个表格
	**/
	var resizeGrid = function(gridId, count) {
		$("#" + gridId + "-wapper img").load(function() {
			count--;
			if(count==0) {
				$("#" + gridId).datagrid("resize");
			}
		});
	}
	
	/**
		点击各个搜索按钮显示搜索结果
	**/
	$("#awaitSearchButton").click(function() {
		showAwaitGrid();
		changeTableBar("awaitList");
	});
	$("#acceptSearchButton").click(function() {
		showAcceptGrid();
		changeTableBar("acceptList");
	});
	$("#denySearchButton").click(function() {
		showDenyGrid();
		changeTableBar("denyList");
	});
	
	/**
		审核通过
	**/
	function acceptThemes(_ids) {
		var prarm = {
			ids:_ids
		}
		if ($("#needLimitFree").attr("checked") == "checked") {
			prarm.needFreeLimit = $("#needLimitFree").attr("checked");
			prarm.discountStart = $("input[name='discountStart']").val();
			if (prarm.discountStart == "") {
				alert("请选择限免开始时间");
				return false;
			}
			prarm.discountLong = $("#discountLong").val();
			if (prarm.discountLong == "") {
				alert("请选择限免开始时间");
				return false;
			}
		}
		$("body").mask("审核通过需要生成手机用的zip包，请耐心等待");
		$.post(acceptUrl, prarm, function(data) {
			$("body").unmask();
			if (data.error) {
				alert(data.error);
			}
			reloadAll();
		});
		return true;
	}
	
	/**
		审核拒绝
	**/
	function denyThemes(_ids, reason) {
		reason = stringFilter(reason);
		$.get(denyUrl + "?ids=" + _ids + "&reason=" + encodeURIComponent(reason), function(data) {
			if (data.error) {
				alert(data.error);
			}
			reloadAll();
		});
	}
	
	/**
		从grid中获取选择的theme行
	**/
	function getIdsFromGrid(grid_id) {
		var rows = $("#" + grid_id).datagrid("getSelections");
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
	
	/**
		获得一个字符串字节长度
	**/
	function getByteLen(val) { 
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
		选择填写拒绝的原因
	**/
	function getDenyReason() {
		var reason = [];
		$(".denyReasonCheck").each(function(){
			var $this = $(this);
			var isCheckde = $this.find("input").attr("checked");
			if (isCheckde) {
				if ($this.text() == "其他") {
					reason.push($this.text() + ":" + $("#denyReason").val());
				} else {
					reason.push($this.text());
				}
			}
		});
		var str = reason.join(",");
		if (str.length > 200) {
			alert("字数不能超过200");
			return "error";
		}
		return str;
	}
	
	/**
		点击弹出填写拒绝原因对话框
	**/
	function openDenyReasonDialog(title,ids,index,gridId) {
		$('#denyReasonDialog').show();
		$('#denyReasonDialog').dialog({
		    title: title,  
		    width: 400,  
		    height: 370,  
		    closed: false,  
		    cache: false,  
		    modal: true,
		    buttons:[{  
		        text:'取消',  
		        handler:function(){  
		        	$('#denyReasonDialog').dialog("close");
		        	unSelectRow(gridId,index);
		        }  
		    },{
		        text:'拒绝该主题',
		        handler:function(){
		        	if (getDenyReason() == "error") {
		        		return;
		        	}
		        	denyThemes(ids,getDenyReason());
		        	$('#denyReasonDialog').dialog("close");
		        }
		    }],
		    onClose:function() {
		    	unSelectRow(gridId,index);
		    }
		}); 
	}
	
	/**
		未审核列表
	**/
	function showAwaitGrid() {
		$("#awaitList").datagrid(gridUtils.createGridOption({
			url:awaitListUrl + "?keyword="+encodeURI(encodeURI($("#awaitSearch").val())),
			columns:[[
				{field:'_id',hidden:true},
				{field:'checkbox',checkbox:true},
				{field:'name',title:'主题名称',width:120,sortable:true,formatter:function(value,row,index) {
					return '<a class="showThemeDetail" href="javascript:void(0)">' + value + '</a><input class="row" type="hidden" value='+row._id+'|'+index+'|awaitList>';
				}},
				{field:'cover',title:'封面图',width:80,formatter: function(value,row,index) {
					return '<a class="showThemeDetail" href="javascript:void(0)"><img width="70" src="'+value+'"/></a><input class="row" type="hidden" value='+row._id+'|'+index+'|awaitList>';
				}},
				{field:'uploadUser',title:'上传者',width:120,sortable:true,formatter:function(value){
					var temp = ""
					if (value != undefined&&value.userName) {
						temp = value.userName;
					}
					return temp;
				}},
				{field:'createAt',title:'上传时间',width:150,sortable:true,formatter:function(value){
					var temp = new Date(value);
					return temp.format("yyyy-MM-dd HH:mm:ss");
				}},
				{field:'payType',title:'付费方式',width:100,sortable:true},
				{field:'action',title:'操作',width:150,formatter: function(value,row,index) {
					return '<a class="acceptButton" href="javascript:void(0)">通过</a>&nbsp;&nbsp;<a class="denyButton" href="javascript:void(0)">拒绝</a><input type="hidden" class="themeId" value="'+row._id+'"><input type="hidden" class="index" value="'+index+'">';
				}}
			]],toolbar:[{
				id:'acceptAll',
				text:'批量通过',
				iconCls:'icon-ok',
				handler:function(){
					var ids = getIdsFromGrid("awaitList");
					if(ids){
						if ($("#themeType").val() == "screenlocker") {
							acceptThemes(ids);
							reloadAll();
						} else {
							openAcceptDialog("是否需要限免",ids,null,"awaitList");
						}
					}
				}
			},{
				id:'denyAll',
				text:'批量拒绝',
				iconCls:'icon-no',
				handler:function(){
					var ids = getIdsFromGrid("awaitList");
					if(ids == null) {
						return;
					}
					openDenyReasonDialog("审核拒绝原因",ids);
				}
			}],
			onLoadSuccess:function() {
				$(".acceptButton").linkbutton({iconCls:'icon-ok'});
				$(".denyButton").linkbutton({iconCls:'icon-no'});
				$(".acceptButton").click(function() {
					var _id = $(this).parent().find(".themeId").val();
					var index = $(this).parent().find(".index").val();
					if ($("#themeType").val() == "screenlocker") {
							acceptThemes(_id);
							reloadAll();
					} else {
						openAcceptDialog("是否需要限免",_id,index,"awaitList");
					}
				});
				$(".denyButton").click(function() {
					var _id = $(this).parent().find(".themeId").val();
					var index = $(this).parent().find(".index").val();
					openDenyReasonDialog("审核拒绝原因",_id, index, "awaitList");
				});
				$(".showThemeDetail").click(function() {
					var row = $(this).parent().find(".row").val();
					var _id = row.split("|")[0];
					var index = row.split("|")[1];
					var grid = row.split("|")[2];
					var name = $(this).text();
					showDetail(detailUrl, _id, grid, index);
				});
				resizeGrid("awaitList",$("#awaitList").datagrid("getRows").length);
			}
		}))
	}
	$('#acceptDialog').dialog({
		closed: true, 
		width: 400,  
	    height: 370, 
	    cache: false,  
	    modal: true
	})
	$("#needLimitFree").change(function() {
		$(".toggle").toggle();
	})
	function openAcceptDialog(title,ids,index,gridId) {
		$("#needLimitFree").attr("checked", false);
		$(".toggle").hide();
		$('#acceptDialog').dialog({
		    title: title,  
		    buttons:[{  
		        text:'取消',  
		        handler:function(){  
		        	$('#acceptDialog').dialog("close");
		        	unSelectRow(gridId,index);
		        }  
		    },{
		        text:'审核通过',
		        handler:function(){
		        	if (getDenyReason() == "error") {
		        		return;
		        	}
		        	var flag = acceptThemes(ids);
		        	if(flag) {
		        		$('#acceptDialog').dialog("close");
		        	}
		        }
		    }],
		    onClose:function() {
		    	unSelectRow(gridId,index);
		    }
		}); 
		$('#acceptDialog').dialog("open");
	}
	
	/**
		已审核通过列表
	**/
	function showAcceptGrid() {
		$("#acceptList").datagrid({
			url:acceptListUrl + "?keyword="+encodeURI(encodeURI($("#acceptSearch").val())),
			width:880,
			fitColumns: true,
			loadMsg:"加载中",
			pageNumber:1,
			pageSize:20,
			pagination:true,
			sortName: 'createAt',
			sortOrder: 'desc',
			remoteSort: true,
			columns:[[
				{field:'_id',hidden:true},
				{field:'checkbox',checkbox:true},
				{field:'name',title:'主题名称',width:120,sortable:true,formatter:function(value,row,index) {
					return '<a class="showThemeDetail2" href="javascript:void(0)">' + value + '</a><input class="row" type="hidden" value='+row._id+'|'+index+'|acceptList>';
				}},
				{field:'cover',title:'封面图',width:120,formatter: function(value,row,index){
					return '<a class="showThemeDetail2" href="javascript:void(0)"><img width="70" src="'+value+'"/></a><input class="row" type="hidden" value='+row._id+'|'+index+'|awaitList>';
				}},
				{field:'uploadUser',title:'上传者',width:120,sortable:true,formatter:function(value){
					var temp = "";
					if (value != undefined&&value.userName) {
						temp = value.userName;
					}
					return temp;
				}},
				{field:'createAt',title:'上传时间',width:150,sortable:true,formatter:function(value){
					var temp = new Date(value);
					return temp.format("yyyy-MM-dd HH:mm:ss");
				}},
				{field:'payType',title:'付费方式',width:100,sortable:true},
				{field:'downloadCount',title:'下载次数',width:120,sortable:true}
//				,
//				{field:'action',title:'操作',width:120,formatter: function(value,row,index) {
//					return '<a class="downlineButton" href="javascript:void(0)">下线</a><input type="hidden" class="themeId" value="'+row._id+'"><input type="hidden" class="index" value="'+index+'">';
//				}}
			]],
//			toolbar:[
//				{
//				id:'downlineAll',
//				text:'批量下线',
//				iconCls:'icon-no',
//				handler:function(){
//					var ids = getIdsFromGrid("acceptList");
//					if(ids == null) {
//						return;
//					}
//					openDenyReasonDialog("下线原因",ids);
//				}
//			}],
			onLoadSuccess:function() {
				$(".downlineButton").linkbutton({iconCls:'icon-no'});
				$(".downlineButton").click(function() {
					var _id = $(this).parent().find(".themeId").val();
					var index = $(this).parent().find(".index").val();
					openDenyReasonDialog("下线原因",_id, index, "acceptList");
				});
				$(".showThemeDetail2").click(function() {
					var row = $(this).parent().find(".row").val();
					var _id = row.split("|")[0];
					var index = row.split("|")[1];
					var grid = row.split("|")[2];
					var name = $(this).text();
					showDetail(detailUrl, _id, grid, index);
				});
				resizeGrid("acceptList",$("#acceptList").datagrid("getRows").length);
			}
		});
	}
	
	/**
		已审核未通过列表
	**/
	function showDenyGrid() {
		$("#denyList").datagrid({
			url:denyListUrl + "?keyword="+encodeURI(encodeURI($("#denySearch").val())),
			width:880,
			fitColumns: true,
			loadMsg:"加载中",
			pageNumber:1,
			pageSize:20,
			pagination:true,
			sortName: 'createAt',
			sortOrder: 'desc',
			remoteSort: true,
			columns:[[
				{field:'_id',hidden:true},
				{field:'info',hidden:true},
				{field:'checkbox',checkbox:true},
				{field:'name',title:'主题名称',width:120,sortable:true,formatter:function(value,row,index) {
					return '<a class="showThemeDetail3" href="javascript:void(0)">' + value + '</a><input class="row" type="hidden" value='+row._id+'|'+index+'|denyList>';
				}},
				{field:'cover',title:'封面图',width:120,formatter: function(value,row,index){
					return '<a class="showThemeDetail3" href="javascript:void(0)"><img width="70" src="'+value+'"/></a><input class="row" type="hidden" value='+row._id+'|'+index+'|awaitList>';
				}},
				{field:'uploadUser',title:'上传者',width:120,sortable:true,formatter:function(value){
					var temp = "";
					if (value != undefined&&value.userName) {
						temp = value.userName;
					}
					return temp;
				}},
				{field:'createAt',title:'上传时间',width:150,sortable:true,formatter:function(value){
					var temp = new Date(value);
					return temp.format("yyyy-MM-dd HH:mm:ss");
				}},
				{field:'payType',title:'付费方式',width:100,sortable:true},
				{field:'action',title:'未通过原因',width:150,formatter: function(value,row,index) {
					return '<a class="lookReasonButton" href="javascript:void(0)">查看原因</a><a href="javascript:void(0)" class="realDel">彻底删除</a><input type="hidden" class="themeId" value="'+row._id+'"><input type="hidden" class="reason" value="'+row.info.reason+'"><input type="hidden" class="index" value="'+index+'">';
				}}
			]],
			onLoadSuccess: function() {
				$(".lookReasonButton").linkbutton();
				$(".lookReasonButton").click(function() {
					var reason = $(this).parent().find(".reason").val();
					if (!reason || reason === "undefined") {reason = "无";}
					$("#showDenyReasonDialog").html(reason.split(",").join("<br>"));
					$("#showDenyReasonDialog").show();
					var index = $(this).parent().find(".index").val();
					$("#showDenyReasonDialog").dialog({
						title:"审核拒绝原因",
						width:200,
					    cache: false,  
					    modal: true,
					    onClose:function() {
					    	unSelectRow("denyList",index);
					    }
					});
				});
				if (realDelUrl != "") {
					$(".realDel").linkbutton().click(function() {
						if (confirm("该功能会将主题进行物理删除，确定要删除么？")) {
							var delId = $(this).parent().find(".themeId").val();
							$.get(realDelUrl + "?ids=" + delId, function(data) {
								if (data.error) {
									alert(data.error);
									return;
								}
								showDenyGrid();
							})
						}
					});
				} else {
					$(".realDel").remove();
				}
				$(".showThemeDetail3").click(function() {
					var row = $(this).parent().find(".row").val();
					var _id = row.split("|")[0];
					var index = row.split("|")[1];
					var grid = row.split("|")[2];
					var name = $(this).text();
					showDetail(detailUrl, _id, grid, index);
				});
				resizeGrid("denyList",$("#denyList").datagrid("getRows").length);
			}
		});
	}
	changeTableBar("awaitList");
	changeTableBar("acceptList");
	changeTableBar("denyList");
})