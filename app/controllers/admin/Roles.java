package controllers.admin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.AdminRole;
import models.User;
import models.Menu;

import org.apache.commons.lang.StringUtils;

import controllers.wither.LogPrinter;
import play.mvc.Controller;
import play.mvc.With;
import utils.EasyMap;
/**
 * 角色管理.
 * @author jiwei
 * @since 2013-7-14
 */
@With({Secure.class, LogPrinter.class })
public class Roles extends Controller {
	/**
	 * 角色管理界面.
	 */
	public static void roleManager() {
		render();
	}
	
	/**
	 * 角色列表.
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
		List<AdminRole> roles = AdminRole.find().offset((page - 1) * rows)
				.limit(rows).order(sortTemp).asList();
		long total = AdminRole.count();
		result.put("rows", roles);
		result.put("total", total);
		System.out.println("=============total="+total);
		renderJSON(result, new play.modules.morphia.utils.ObjectIdGsonAdapter());
	}
	/**
	 * 新增一个角色.
	 * @param roleName 角色名称
	 * @param roleDesc 角色描述
	 * @param menuIds 角色对应的菜单权限ID
	 */
	public static void add(final String roleName,
			final String roleDesc, final String menuIds) {
		
		if (StringUtils.isBlank(roleName)) {
			renderJSON(new EasyMap("error", "角色名不能为空"));
		}
		if (AdminRole.filter("roleName", roleName).count() > 0) {
			renderJSON(new EasyMap("error", "该角色名已存在"));
		}
		AdminRole role = new AdminRole();
		role.roleName = roleName;
		if (StringUtils.isNotBlank(roleDesc)) {
			role.roleDesc = roleDesc;
		}
		if (StringUtils.isNotBlank(menuIds)) {
			String[] menus = menuIds.split(",");
			Set<Menu> menuList = new HashSet<Menu>();
			for (int i = 0; i < menus.length; i++) {
				Menu menu = Menu.findById(menus[i]);
				if (menu == null) {
					continue;
				}
				menuList.add(menu);
			}
			if (menuList.size() > 0) {
				role.menuList = menuList;
			}
		}
		role.save();
		renderJSON(new EasyMap("success", "新增成功"));
	}
	/**
	 * 删除一个角色.
	 * @param roleIds 角色ID
	 */
	public static void delete(final String roleIds) {
		if (StringUtils.isBlank(roleIds)) {
			renderJSON(new EasyMap("error", "请选择要删除的角色"));
		}
		EasyMap result = new EasyMap("", "");
		String[] ids = roleIds.split(",");
		for (int i = 0; i < ids.length; i++) {
			AdminRole role = AdminRole.findById(ids[i]);
			if (role == null) {
				continue;
			}
			if (role.roleLevel == 100) {
				result.put("warning", "超级管理员不能删除");
				continue;
			}
			List<User> users = User.filter("adminRole", role).asList();
			for (int j = 0; j < users.size(); j++) {
				User user = users.get(j);
				user.adminRole = null;
				user.save();
			}
			role.delete();
		}
		result.put("success", "删除成功");
		renderJSON(result);
	}
	/**
	 * 获取一个角色的详情.
	 * @param roleId 角色ID
	 */
	public static void detail(final String roleId) {
		if (StringUtils.isBlank(roleId)) {
			renderJSON(new EasyMap("error", "请选择要修改的角色"));
		}
		AdminRole role = AdminRole.findById(roleId);
		if (role == null) {
			renderJSON(new EasyMap("error", "该角色不存在"));
		}
		renderJSON(role, new play.modules.morphia.utils.ObjectIdGsonAdapter());
	}
	/**
	 * 修改一个角色.
	 * @param roleId 要修改的角色Id
	 * @param roleName 角色名称
	 * @param roleDesc 角色描述
	 * @param menuIds 角色对应的菜单权限ID
	 */
	public static void update(final String roleId, final String roleName,
			final String roleDesc, final String menuIds) {
		if (StringUtils.isBlank(roleId) || AdminRole.findById(roleId) == null) {
			renderJSON(new EasyMap("error", "请选择要修改的角色"));
		}
		AdminRole role = AdminRole.findById(roleId);
		if (StringUtils.isBlank(roleName)) {
			renderJSON(new EasyMap("error", "角色名不能为空"));
		}
//		if (role.roleLevel == 100) {
//			renderJSON(new EasyMap("warning", "超级管理员不能修改"));
//		}
		role.roleName = roleName;
		role.roleDesc = roleDesc;
		Set<Menu> menus = new HashSet<Menu>();
		if (StringUtils.isNotBlank(menuIds)) {
			String[] ids = menuIds.split(",");
			for (int i = 0; i < ids.length; i++) {
				Menu menu = Menu.findById(ids[i]);
				if (menu != null) {
					menus.add(menu);
				}
			}
			if (menus.size() > 0) {
				role.menuList = menus;
			}
		} else {
			role.menuList = menus;
		}
		role.save();
		renderJSON(new EasyMap("success", "更新成功"));
	}
}
