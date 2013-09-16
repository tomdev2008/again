package controllers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Menu;

import org.apache.commons.lang.StringUtils;

import controllers.wither.LogPrinter;
import play.mvc.Controller;
import play.mvc.With;
import service.TreeService;
import utils.EasyMap;
/**
 * 菜单管理.
 * @author jiwei
 * @since 2013-7-14
 */
@With({Secure.class, LogPrinter.class })
public class Menus extends Controller {
	/**
	 * 菜单管理界面.
	 */
	public static void menuManager() {
		render();
	}
	
	/**
	 * 生成菜单树.
	 */
	public static void tree() {
		List<Menu> roots = Menu.filter("parent exists", false).order("order").asList();
		List<Map> result = new ArrayList();
		TreeService.createTree(roots, result, false);
		renderJSON(result);
	}
	/**
	 * 新增一个菜单.
	 * @param menuName 菜单名称
	 * @param url 菜单指向URL
	 * @param parentMenuId 父菜单ID
	 */
	public static void add(final String menuName,
			final String url, final String parentMenuId) {
//		List<Menu> menus = Menu.filter("menuName", menuName).asList();
//		if (menus.size() > 0) {
//			renderJSON(new EasyMap("error", "该菜单名已存在，请重新填写！"));
//		}
		if (StringUtils.isBlank(menuName)) {
			renderJSON(new EasyMap("error", "请输入菜单名！"));
		}
 		Menu menu = new Menu();
		menu.menuName = menuName;
		if (!StringUtils.isBlank(url)) {
			menu.url = url;
		}
		menu.order = 0l;
		if (StringUtils.isNotBlank(parentMenuId)) {
			Menu parent = Menu.findById(parentMenuId);
			if (parent != null) {
				menu.parent = parent;
			}
		}
		menu.save();
		renderJSON(new EasyMap("success", "新增成功"));
	}
	/**
	 * 删除一个菜单及其子菜单.
	 * @param menuId 菜单ID
	 */
	public static void del(final String menuId) {
		if (StringUtils.isBlank(menuId)) {
			renderJSON(new EasyMap("error", "请选择要删除的菜单"));
		}
		if (Menu.findById(menuId) == null) {
			renderJSON(new EasyMap("error", "请选择要删除的菜单"));
		}
		TreeService.delTree(menuId);
		renderJSON(new EasyMap("success", "删除成功"));
	}
	/**
	 * 根据一个菜单ID 返回菜单详情.
	 * @param menuId
	 */
	public static void detail(final String menuId) {
		if (StringUtils.isBlank(menuId)) {
			renderJSON(new EasyMap("error", "请选择要修改的菜单"));
		}
		Menu menu = Menu.findById(menuId);
		if (menu == null) {
			renderJSON(new EasyMap("error", "请选择要修改的菜单"));
		}
		renderJSON(menu, new play.modules.morphia.utils.ObjectIdGsonAdapter());
	}
	/**
	 * 修改一个菜单.
	 * @param menuId menuId
	 */
	public static void update(final String menuId, final String menuName, final String url, final long order) {
		if (StringUtils.isBlank(menuId)) {
			renderJSON(new EasyMap("error", "请选择要更新的菜单"));
		}
		if (StringUtils.isBlank(menuName)) {
			renderJSON(new EasyMap("error", "请输入菜单名！"));
		}
		Menu menu = Menu.findById(menuId);
		if (menu == null) {
			renderJSON(new EasyMap("error", "请选择要更新的菜单"));
		}
		menu.menuName = menuName;
		menu.url = url;
		menu.order = order;
		menu.save();
		renderJSON(new EasyMap("success", "更新成功"));
	}
}
