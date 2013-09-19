package controllers.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.AdminRole;
import models.User;

import org.apache.commons.lang.StringUtils;

import controllers.admin.Secure;
import controllers.wither.LogPrinter;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;
import service.UserService;
import utils.EasyMap;


@With({Secure.class, LogPrinter.class })
public class Users extends Controller {
	
	public static void add(){
		UserService.getUser("admin");
		renderText("ok");
	}
	
	/**
	 * 用户列表.
	 * @param rows 一页显示条数
	 * @param page 当前页数
	 * @param order 排序方式asc desc
	 * @param sort 排序字段
	 */
	public static void list(final int rows,
			final int page, final String order, final String sort) {
		Map result = new HashMap();
		String sortTemp = sort;
		if ("asc".equals(order)) {
			sortTemp = "-" + sort;
		}
		List<User> users = User.find().offset((page - 1) * rows)
				.limit(rows).order(sortTemp).asList();
		long total = User.count();
		result.put("rows", users);
		result.put("total", total);
		renderJSON(result, new play.modules.morphia.utils.ObjectIdGsonAdapter());
	}
	/**
	 * 用户的添加.
	 * @param userName 用户名
	 * @param password 用户密码
	 * @param roleId 用户角色
	 */
	public static void add(final String userName,
			final String password, final String roleId) {
		if (StringUtils.isBlank(userName)) {
			renderJSON(new EasyMap("error", "用户名不能为空"));
		}
		if (User.filter("userName", userName).count() > 0) {
			renderJSON(new EasyMap("error", "该用户名已存在"));
		}
		if (StringUtils.isBlank(password)) {
			renderJSON(new EasyMap("error", "密码不能为空"));
		}
		User user = new User(userName, password);
		if (StringUtils.isNotBlank(roleId)) {
			AdminRole role = AdminRole.findById(roleId);
			if (role != null) {
				user.adminRole = role;
			}
		}
		user.save();
		renderJSON(new EasyMap("success", "新增成功"));
	}
	/**
	 * 删除一个用户.
	 * @param userIds 用户ID
	 */
	public static void delete(final String userIds) {
		if (StringUtils.isBlank(userIds)) {
			renderJSON(new EasyMap("error", "请选择要删除的用户"));
		}
		EasyMap result = new EasyMap("", "");
		String[] ids = userIds.split(",");
		for (int i = 0; i < ids.length; i++) {
			User user = User.findById(ids[i]);
			if (user == null) {
				continue;
			}
			if (user.adminRole != null) {
				if (user.adminRole.roleLevel == 100) {
					result.put("warning", "超级管理员用户不能删除");
					continue;
				}
			}
			user.delete();
		}
		result.put("success", "删除成功");
		renderJSON(result);
	}
	/**
	 * 获取一个用户详情.
	 * @param userId 用户ID
	 */
	public static void detail(final String userId) {
		if (StringUtils.isBlank(userId)) {
			renderJSON(new EasyMap("error", "请选择要修改的用户"));
		}
		User user = User.findById(userId);
		if (user == null) {
			renderJSON(new EasyMap("error", "该用户不存在"));
		}
		renderJSON(user, new play.modules.morphia.utils.ObjectIdGsonAdapter());
	}
	/**
	 * 更新一个用户的角色信息.
	 * @param userId 用户ID
	 * @param roleId 角色ID
	 */
	public static void update(final String userId, final String roleId) {
		if (StringUtils.isBlank(userId)) {
			renderJSON(new EasyMap("error", "用户不存在"));
		}
		User user = User.findById(userId);
		if (user == null) {
			renderJSON(new EasyMap("error", "用户不存在"));
		}
		if (user.adminRole != null && user.adminRole.roleLevel == 100) {
			renderJSON(new EasyMap("warning", "超级管理员用户不能修改"));
		}
		if (StringUtils.isNotBlank(roleId)) {
			AdminRole role = AdminRole.findById(roleId);
			if (role == null) {
				renderJSON(new EasyMap("error", "该角色不存在"));
			}
			user.adminRole = role;
		} else {
			user.adminRole = null;
		}
		user.save();
		renderJSON(new EasyMap("success", "修改成功"));
	}
	/**
	 * 用户更改密码.
	 * @param pass 用户原密码
	 * @param newPass 用户新密码
	 * @param confirmPass 用户新密码确认
	 */
	public static void updatePass(final String pass,
			final String newPass, final String confirmPass) {
		if (StringUtils.isBlank(pass) || StringUtils.isBlank(newPass)
				|| StringUtils.isBlank(confirmPass)) {
			renderJSON(new EasyMap("error", "密码不能为空"));
		}
		User user = Secure.getLoginUser();
		if (user == null) {
			Secure.login();
		}
		if (!user.password.equals(Codec.hexMD5(pass))) {
			renderJSON(new EasyMap("error", "请输入正确的密码"));
		}
		if (!newPass.equals(confirmPass)) {
			renderJSON(new EasyMap("error", "两次输入新密码不同，请重新输入"));
		}
		user.password = Codec.hexMD5(newPass);
		user.save();
		renderJSON(new EasyMap("success", "修改成功"));
	}
	
	
	/**
	 * 用户管理界面.
	 */
	public static void userManager() {
		render();
	}
}