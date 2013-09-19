package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Course;
import models.SubjectSource;
import models.Tag;
import play.mvc.Util;

public abstract class CommonTreeService<T> {

	/**
	 * 使用递归算法来生成所需要的Tree.
	 * @param roots 根节点
	 * @param result 生成tree所需要的JSON串
	 * @param isLinked 点击树是否跳转相应的链接
	 * @param noShowInManager 是否在菜单管理中显示
	 */
	@Util
	public static <T>void createTree(final List<T> roots,
			final List<Map> result) {
		for (T t:roots) {
			Map node = addNode(t);
			List<T> children =getNode(t);
			List<Map> treeChildren = new ArrayList();
			createTree(children, treeChildren);
			node.put("children", treeChildren);
			result.add(node);
		}
	}
	
	public static  <T> List<T> getNode(T t){
		List children = null;
		if(t.getClass().equals(Tag.class)){
			children = Tag.filter("context", t).order("index").asList();
			
		}else if(t.getClass().equals(Course.class)){
			children = Course.filter("context", t).order("index").asList();
			
		}else if(t.getClass().equals(SubjectSource.class)){
			children = SubjectSource.filter("context", t).order("index").asList();
		}
		return children;
	}
	
	public static  <T>  Map addNode(T t){
		Map node = new HashMap();
		if(t.getClass().equals(Tag.class)){
			Tag tag = (Tag)t;
			node.put("id", tag.getId().toString());
			node.put("text", tag.name);
		}else if(t.getClass().equals(Course.class)){
			Course tag = (Course)t;
			node.put("id", tag.getId().toString());
			node.put("text", tag.name);
		}else if(t.getClass().equals(SubjectSource.class)){
			SubjectSource tag = (SubjectSource)t;
			node.put("id", tag.getId().toString());
			node.put("text", tag.name);
		}
		return node;
	}
	
	/**
	 * 递归删除树.
	 * @param menueId 树的父亲节点ID
	 */
	public static <T> void delTree(T t) {
		if(t.getClass().equals(Tag.class)){
			Tag tag = (Tag)t;
			List<Tag> children = Tag.filter("context", tag).asList();
			for (Tag child:children) {
				delTree(child);
			}
			tag.delete();
		}else if(t.getClass().getClass().equals(Course.class)){
			Course tag = (Course)t;
			List<Course> children = Course.filter("context", tag).asList();
			for (Course child:children) {
				delTree(child);
			}
			tag.delete();
		}else if(t.getClass().equals(SubjectSource.class)){
			SubjectSource tag = (SubjectSource)t;
			List<SubjectSource> children = SubjectSource.filter("context", tag).asList();
			for (SubjectSource child:children) {
				delTree(child);
			}
			tag.delete();
		}
	}
}
