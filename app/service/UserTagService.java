package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import controllers.admin.Secure;
import models.UserTag;
import models.Tag;
import models.User;
import play.mvc.Util;

public class UserTagService {

	
	/**
	 * 使用递归算法来生成所需要的Tree.
	 * @param roots 根节点
	 * @param result 生成tree所需要的JSON串
	 * @param isLinked 点击树是否跳转相应的链接
	 * @param noShowInManager 是否在菜单管理中显示
	 */
	@Util
	public static void createTree(final List<UserTag> roots,
			final List<Map> result) {
		
		for (UserTag root:roots) {
			Map node = new HashMap();
			node.put("id", root.getId());
			node.put("name", root.tag.name);
			node.put("score", root.score);
			node.put("doneCnt", root.doneCnt);
			node.put("cr", root.CR);
			node.put("subjectCnt", root.subjectCnt);
			List<UserTag> children = UserTag.filter("context", root).order("tag.index").asList();
			List<Map> treeChildren = new ArrayList();
			createTree(children, treeChildren);
			node.put("children", treeChildren);
			result.add(node);
		}
	}
}
