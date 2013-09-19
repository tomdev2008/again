// 保存广告位
function adSubmit() {
    var v = $("#b_ad_dlg input[name=code]").val();
    if (!v) {
        alert('请输入代号！');
        return false;
    }
    v = $("#b_ad_dlg input[name=name]").val();
    if (!v) {
        alert('请输入名称！');
        return false;
    }
    return true;
}
// 保存结果回调
function save_finish(r) {
    r = r || {};
    if (r.error) {
        alert(r.error);
    } else {
        window.location.reload();
    }
}
/**
 * 删除一个广告位
 * @param no 广告位的序号
 */
function del_ad(no) {
    if (confirm("你确定要删除此广告位吗？")) {
        $.post(CONSTANT.PATH + "ajax/admins/ads/del", {no:no}, function() {
            window.location.reload();
        })
    }
}
$(document).ready(function() {
    $(".blank-ad").click(function() {
        $("#b_ad_dlg").dialog({
            modal:true,
            width: 500,
            height: 400,
            buttons: {
                "确定": function() {
                    $("#b_ad_dlg form").submit();
                },
                "取消": function() {
                    $(this).dialog("close");
                }
            }
        });
        $(".no").val($(this).attr("data"));
    })
});