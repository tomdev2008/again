package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.AdminRole;
import models.Menu;
import models.User;

import org.apache.commons.lang.StringUtils;

import controllers.admin.Secure;

import play.mvc.Util;
/**
 * 生成EasyUI的tree的共用工具类.
 * @author jiwei
 * @since 2013-7-14
 */
public class TreeService {
	/**
	 * 使用递归算法来生成所需要的Tree.
	 * @param roots 根节点
	 * @param result 生成tree所需要的JSON串
	 * @param isLinked 点击树是否跳转相应的链接
	 * @param noShowInManager 是否在菜单管理中显示
	 */
	@Util
	public static void createTree(final List<Menu> roots,
			final List<Map> result, final boolean isLinked) {
		User user = Secure.getLoginUser();
		Set<Menu> menus = user.adminRole.menuList;
		for (Menu root:roots) {
			Map node = new HashMap();
			node.put("id", root.getId().toString());
			node.put("text", root.menuName);
			if (isLinked) {
				if (user.adminRole.roleLevel != 100) {
					boolean isIn = false;
					for (Menu menu:menus) {
						if (root.getId().toString().equals(menu.getId().toString())) {
							isIn = true;
							break;
						}
					}
					if (!isIn) {
						continue;
					}
				}
				if (!StringUtils.isBlank(root.url) && !"/".equals(root.url)) {
					node.put("text", "<a href=" +root.url + " target='mainFrame'>" + root.menuName + "</a>");
				}
			}
			List<Menu> children = Menu.filter("parent", root).order("order").asList();
			List<Map> treeChildren = new ArrayList();
			if (isLinked) {
				createTree(children, treeChildren, true);
			} else {
				createTree(children, treeChildren, false);
			}
			node.put("children", treeChildren);
			result.add(node);
		}
	}
	/**
	 * 递归删除树.
	 * @param menueId 树的父亲节点ID
	 */
	public static void delTree(final String menueId) {
		Menu menu = Menu.findById(menueId);
		List<Menu> children = Menu.filter("parent", menu).asList();
		List<AdminRole> roles = AdminRole.filter("menuList", menu).asList();
		for (AdminRole role:roles) {
			role.menuList.remove(menu);
			role.save();
		}
		menu.delete();
		for (Menu child:children) {
			delTree(child.getId().toString());
		}
	}
}
