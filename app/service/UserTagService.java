//package service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import models.Tag;
//import models.UserTag;
//import play.mvc.Util;
//
//public class UserTagService {
//
//	
//	/**
//	 * 使用递归算法来生成所需要的Tree.
//	 * @param roots 根节点
//	 * @param result 生成tree所需要的JSON串
//	 * @param isLinked 点击树是否跳转相应的链接
//	 * @param noShowInManager 是否在菜单管理中显示
//	 */
//	@Util
//	public static void createTree(final List<UserTag> childrens,
//			final List<Map> result) {
//		for (UserTag root:roots) {
//			Map node = new HashMap();
//			node.put("id", root.getId().toString());
//			node.put("text", root.name);
//			List<UserTag> children = Tag.filter("context", root).order("index").asList();
//			List<Map> treeChildren = new ArrayList();
//			
//			node.put("children", treeChildren);
//			result.add(node);
//		}
//	}
//}
