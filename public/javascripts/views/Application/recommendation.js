$(document).ready(function() {
    var curli;
    $('#tt').datagrid({
        columns:[[
            {field:'name', title:'名称', width:80},
            {field:'previews',title:'预览图', width:300,
                formatter: function(value,row,index){
                    if (row.previews){
                        var cc = [];
                        cc.push('<img style="float: left;width:100px" src="'+func_cdnproxy({url:encodeURIComponent(row['previews'][0])})+'">');
                        cc.push('<img style="float: left;width:100px" src="'+func_cdnproxy({url:encodeURIComponent(row['previews'][1])})+'">');
                        return cc.join('');
                    } else {
                        return value;
                    }
                }
            }
        ]],
        onClickRow: function(rowIndex, rowData) {
            if (confirm("你确认要推荐此主题吗？")) {
                $.post(CONSTANT.PATH + "ajax/themes/recommend", {id:rowData["_id"], no: curli.attr("data")}, function() {
                    window.location.reload();
                });
            }
        }
    });

    // “点击选择主题”按钮
    $("#p_theme_recommendation li").click(function() {
        var left = $(window).width() / 2 - 210, top = ($(window).height() / 2 + $(window).scrollTop()) - 200;
        $("#b_dlg").css({
        	visibility: 'visible',
        	left: left + 'px',
        	top: top + 'px'}).show();
        curli = $(this);
    });
    // “点击删除推荐主题”按钮
    $("#p_theme_recommendation .delete").click(function(event) {
        if (confirm("你确定要删除此推荐位吗？")) {
            $.post(CONSTANT.PATH + "ajax/themes/recommend", {id:null, no: $(this).parent().attr("data")}, function() {
                window.location.reload();
            });
        }
        event.stopPropagation();
        return false;
    });

    // 主题选择对话框中的取消按钮点击事件
    $("#b_dlg .cancel").click(function() {
        $("#b_dlg").hide();
    })
});