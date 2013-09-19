/**
 * 主题分类管理.
 * @author zhaojingyu
 * @since 2012-09-13
 */
$(document).ready(function() {
	function showDetail(themeId, themeName, gridId, index) {
		$.get(detailUrl + "?themeId="+themeId, function(theme) {
			var arr = [];
			if (theme.previews) {
				for (var i=0;i<theme.previews.length;i++) {
					arr.push('<img src="'+theme.previews[i]+'" width="310px" height="550px" />');
				}
				$(".pic").html(arr.join(''));
			}
			if (theme.payType == "CHARGE") {
				$("#payType").html(theme.price + "元");
			} else {
				$("#payType").html("免费");
			}
			$("#downloadCount").html(theme.downloadCount);
			$("#size").html((theme.size/1024).toFixed(2) + "K");
			$("#author").html(theme.author);
			$("#themeDesc").html(theme.desc);
			if (theme.categories && theme.categories.length > 0) {
				$("#category-li").remove();
				$("#theme-info").append('<li id="category-li"><span class="k">分类:</span><span id="category" class="v">'+theme.categories[0].name+'</span></li>');
			} else {
				$("#category-li").remove();
			}
		});
		$("#showThemeDetailDialog").show();
		$("#showThemeDetailDialog").dialog({
		    title: "主题详情————" + themeName,
		    width: 1000,  
		    height: 651,  
		    closed: false,  
		    cache: false,  
		    modal: true,
		    buttons:[{
		        text:'关闭',  
		        handler:function(){
		        	$('#showThemeDetailDialog').dialog("close");
		        	unSelectRow(gridId,index);
		        }
		    }],
		    onClose:function() {
		    	unSelectRow(gridId,index);
		    }
		});
	};
	function unSelectRow(gridId,index) {
		setTimeout(function() {
			$("#"+gridId).datagrid("unselectRow",index);
		},200);
	}
	$("#themeSearch").val("");
	/**
	 * 初始化加载所有分类列表.
	 */
	$("#category-manager .easyui-tree").tree({
		url : categoryListUrl,
		onSelect : function(node) {
			$("#themeSearch").val("");
			if (node.id) {
				var name = node.text;
				var id = node.id;
				showThemeGrid(id);
			}
		},onLoadSuccess:function() {
			var node = $('#category-manager .easyui-tree').tree("getRoot");
			$("#category-manager .easyui-tree").tree("select",node.target);
		}
	})
	/**
	 * 按钮easyUI化~
	 */
	$(".manager .button").linkbutton({});
	$(".searchButton").linkbutton({});
	/**
	 * 点击搜索，触发重新加载grid
	 */
	$("#themeSearchButton").click(function () {
		showThemeGrid(getSelectId());
	})
	/**
	 * 主题分类的增删改查操作.
	 */
	$("#category-manager .manager .add").click(function() {
		$("#addCategoryDialog").show();
		$("#addCategoryDialog").dialog({
			title: "新增主题分类",
			    width: 450,
			    height: 300,
			    closed: false,
			    cache: false,
			    modal: true,
			    buttons:[{
			        text:'新增',
			        handler:function() {
			        	$("#addCategoryForm").submit();
			        	$('#addCategoryDialog').dialog("close");
			        }
			    },{
			        text:'关闭',
			        handler:function() {
			        	$('#addCategoryDialog').dialog("close");
			        }
			    }]
		})
	});
	$("#category-manager .manager .look").click(function() {
		var id = getSelectId();
		if (!id || id == "-1" || id == "1") {
			alert("请选择一个分类");
			return;
		}
		showManagerCategory();
	});
	$("#category-manager .manager .stop").click(function() {
		var id = getSelectId();
		if (!id || id == "-1" || id == "1") {
			alert("请选择一个分类");
			return;
		}
		changeCategoryStatus(false);
	});
	$("#category-manager .manager .start").click(function() {
		var id = getSelectId();
		if (!id || id == "-1" || id == "1") {
			alert("请选择一个分类");
			return;
		}
		changeCategoryStatus(true);
	});
	function changeCategoryStatus(flag) {
		$.get(changeCategoryStatusUrl+"?categoryId="+getSelectId()+"&flag="+flag,function(data) {
			if (data.error) {
				alert(data.error);
				return;
			}
			alert(data.success);
		});
	}
	/**
	 * 點擊對話框中的修改按鈕，對主題分類基本信息進行一些修改
	 */
	$("#showCategoryDetailDialog ul li .right .update").click(function() {
		toggleButton($(this), true, false);
		var $center = $(this).parents("li").find(".center");
		var value = $center.text();
		if ($center.hasClass("styleType")) {//如果是类型的话，会选择下拉框
			$.get(categoryTypeUrl,function(data) {
				var arr = [];
				arr.push('<select id="styleType">');
				for (var i=0;i<data.length;i++) {
					arr.push('<option value="');
					arr.push(data[i]);
					arr.push('"');
					if (data[i] == value) {
						arr.push(' selected= "selected"');
					}
					arr.push('>');
					arr.push(data[i]);
					arr.push('</option>');
				}
				arr.push('</select>');
				$center.html(arr.join('') + '<input class="hiddenValue" type="hidden" value="'+$center.text()+'">');
			})
		} else {//如果是不是类型的话，出现文本编辑框
			$center.html('<input class="realValue" type="text" value="'+$center.text()+'"/><input class="hiddenValue" type="hidden" value="'+$center.text()+'">');
		}
	})
	/**
	 * 编辑取消.
	 */
	$("#showCategoryDetailDialog ul li .right .cancel").click(function() {
		var $center = $(this).parents("li").find(".center");
		$center.html($center.find(".hiddenValue").val());
		toggleButton($(this), false, true);
	})
	/**
	 * 编辑保存.
	 */
	$("#showCategoryDetailDialog ul li .right .save").click(function() {
		var $this = $(this);
		var $center = $this.parents("li").find(".center");
		var type = $center.attr("class").split(" ")[1];
		$.post(updateCategoryUrl[type]+"?field="
				+ (type=="styleType"?encodeURI(encodeURI(stringFilter($("#styleType").val()))):encodeURI(encodeURI(stringFilter($center.find(".realValue").val()))))
				+ "&categoryId=" + $("#showCategoryDetailDialog #categoryId").val(), function(data){
			if (data.error) {
				alert(data.error);
				return;
			}
			$center.html((type=="styleType"?$("#styleType").val():$center.find(".realValue").val()));
			toggleButton($this, false, false);
			$("#category-manager .easyui-tree").tree("reload");
		})
	})
	/**
	 * 改变修改主题的按钮状态。
	 */
	function toggleButton(el, flag, flag2) {
		if (flag) {
			el.hide();
			el.next().show();
			el.next().next().show();
		} else if(flag2) {
			el.hide();
			el.prev().hide();
			el.prev().prev().show();
		} else {
			el.hide();
			el.next().hide();
			el.prev().show();
		}
	}
	/**
	 * 获取选中的树的名称，没有选中则返回null.
	 */
	function getSelectName() {
		var node = $('#category-manager .easyui-tree').tree('getSelected');
		if (!node) {
			return null;
		}
		return node.text;
	}
	/**
	 * 获取选中的树的id，没有选中则返回null.
	 */
	function getSelectId() {
		var node = $('#category-manager .easyui-tree').tree('getSelected');
		if (!node) {
			return null;
		}
		return node.id;
	}
	/**
	 * 重新改变grid的长度
	 */
	var resizeGrid = function(gridId, count) {
		$("#" + gridId + "-wapper img").load(function() {
			count--;
			if(count==0) {
				$("#" + gridId).datagrid("resize");
			}
		});
	}
	/**
	 * 显示主题分类的详细信息，以及对各信息的编辑和删除。
	 */
	function showManagerCategory() {
		var show_id = getSelectId();
		$.get(categoryDetailUrl + "?id=" + show_id,function(data) {
			if (data.error) { 
				alert(data.error);
				return;
			}
			$("#showCategoryDetailDialog #categoryId").val(show_id);
			$("#showCategoryDetailDialog .code").html(data.code);
			$("#showCategoryDetailDialog .name").html(data.name);
			if (data.cover) {
				$("#showCategoryDetailDialog .cover .cover-image img").attr("src",data.cover);
			}
			$("#showCategoryDetailDialog .styleType").html(data.styleType);
			$("#showCategoryDetailDialog").show();
			$("#showCategoryDetailDialog").dialog({
				title: "主题分类详情——" + getSelectName(),
			    width: 900,
			    height: 650,
			    closed: false,
			    cache: false,
			    modal: true,
			    buttons:[{
			        text:'删除该分类',
			        handler:function() {
			        	if (confirm("删除该分类会导致该分类下所有主题都归到未分类，确定删除么？")) {
			        		$.get(categoryDelUrl+'?categoryId='+$("#showCategoryDetailDialog #categoryId").val(),function(data) {
			        			if (data.error) {
			        				alert(data.error);
			        				return;
			        			}
			        			$("#category-manager .easyui-tree").tree("reload");
				        	})
				        	$('#showCategoryDetailDialog').dialog("close");
			        	}
			        }
			    },{
			        text:'关闭',
			        handler:function() {
			        	$('#showCategoryDetailDialog').dialog("close");
			        }
			    }],
			    onClose:function () {
			    	toggleButton($("#showCategoryDetailDialog ul li .right .cancel"), false, true);
			    }
			})
		})
	}
	/**
	 * 从grid中获取选择的theme行
	 */
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
	 * 升级主题分类的封面图.
	 */
	$("#updateCover").click(function() {
		$("#updateCoverForm").submit();
	})
	/**
	 * 改变grid中的fotter为中文
	 */
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
	 * 显示分类中的主题列表
	 */
	function showThemeGrid(id) {
		$("#themeData").datagrid({
			url:themeInCategoryUrl + "?categoryId="+id+"&keyword="+encodeURI(encodeURI($("#themeSearch").val())),
			width:900,
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
					return '<a class="showThemeDetail" href="javascript:void(0)">' + value + '</a><input class="row" type="hidden" value='+row._id+'|'+index+'|themeData>';
				}},
				{field:'cover',title:'封面图',width:80,formatter: function(value,row,index) {
					return '<a class="showThemeDetail" href="javascript:void(0)"><img width="70" src="'+value+'"/></a><input class="row" type="hidden" value='+row._id+'|'+index+'|themeData>';
				}},
				{field:'uploadUser',title:'上传者',width:120,sortable:true,formatter:function(value){
					var temp = "";
					if (value) {
						if (value.userName) {
							temp = value.userName;
						}
					}
					return temp;
				}},
				{field:'createAt',title:'上传时间',width:150,sortable:true,formatter:function(value){
					var temp = new Date(value);
					return temp.format("yyyy-MM-dd HH:mm:ss");
				}},
				{field:'payType',title:'付费方式',width:100,sortable:true},
				{field:'action',title:'主题操作',width:100, formatter:function(value,row,index) {
					return '<a href="javascript:void(0)" class="updateThemeInfo">修改主题信息</a><input type="hidden" class="themeId" value="'+row._id+'">';
				}}
			]],toolbar:[{
				id:'changeCategory',
				text:'批量修改分类',
				iconCls:'icon-ok',
				handler:function(){
					var ids = getIdsFromGrid("themeData");
					if(ids){
						getAllCategorys(ids);
					}
				}
			}],onLoadSuccess:function() {
				changeTableBar("themeData");
				$('.updateThemeInfo').linkbutton({});
				$('.updateThemeInfo').click(function() {
					alert("尚未迁移过来");
//					var ThemeId = $(this).parent().find(".themeId").val();
//					window.location.href = themeEditUrl + "?themeId="+ThemeId;
				})
				$(".showThemeDetail").click(function() {
					var row = $(this).parent().find(".row").val();
					var _id = row.split("|")[0];
					var index = row.split("|")[1];
					var grid = row.split("|")[2];
					var name = $(this).text();
					showDetail(_id, name, grid, index);
				});
				resizeGrid("themeData",$("#themeData").datagrid("getRows").length);
			}
		})
	}
	/**
	 * 展示所有的分类.
	 */
	function showCategorys(ids) {
		$("#category-all").show();
		$("#category-all").dialog({
			title: "主题分类选择",
		    width: 300,
		    closed: false,
		    cache: false,
		    modal: true,
		    buttons:[{
		        text:'确定',
		        handler:function() {
		        	var categoryId = '';
		        	$(".category-radio-li input[type='radio']").each(function() {
		        		if ($(this).attr("checked") == "checked") {
		        			categoryId = $(this).parent().find("input[type='hidden']").val();
		        		}
		        	})
		        	changeCategory(categoryId, ids);
		        	$('#category-all').dialog("close");
		        }
		    },{
		        text:'关闭',
		        handler:function() {
		        	$('#category-all').dialog("close");
		        }
		    }]
		})
	}
	/**
	 * 改变批量选择主题的分类.
	 */
	function changeCategory(categoryId, ids) {
		$.get(changeCategoryUrl+"?categoryId="+categoryId+"&ids="+ids, function(data) {
			if(data.error){
				alert(data.error);
				return;
			}
			$("#themeData").datagrid("reload");
		})
	}
	/**
	 * 获取所有的主题分类信息，放在dialog中
	 */
	function getAllCategorys(ids) {
		$.get(getAllCategorysUrl, function(data) {
			var html = [];
			html.push('<ul>');
			for (var i=0;i<data.length;i++) {
				html.push('<li class="category-radio-li"><input name="category-radio" type="radio">');
				html.push(data[i].name);
				html.push('<input type="hidden" value="');
				html.push(data[i]._id);
				html.push('"></li>');
			}
			html.push('<li class="category-radio-li"><input name="category-radio" type="radio">未分类<input type="hidden" value="-1"></li></ul>');
			$("#category-all").html(html.join(''));
			showCategorys(ids);
		})
	}
})
/** 
 * 上传后的回调.
 */
function formSubmitCallback(json) {
	var arr = [];
	for (var i = 0, n; n = json[i]; i ++) {
		arr.push(n.msg);
	}
	if (arr.length == 0) {
		var id = $("#categoryId").val();
		$.get(categoryDetailUrl + "?id=" + id + "&r="+Math.random(),function(data) {
			var img = '<img height=440 width=265 src="'+ data.cover +'"/>';
			$("#showCategoryDetailDialog .cover .cover-image").html(img);
		})
		$("#category-manager .easyui-tree").tree("reload");
	} else {
		alert(arr.join(','))
	}
}