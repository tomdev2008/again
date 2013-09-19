package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import models.Tag;
import models.User;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Util;

public class TagService {

	/**
	 * 
	 * 根据标签名称获取标签，如果没有此标签则创建一个并返回
	 * @param tagName2
	 * @return
	 */
	public static Tag getTag(String name) {
		if(StringUtils.isBlank(name)){
			return null;
		}
		String[] tag = name.split(":");
		MorphiaQuery query = Tag.filter("name",tag[tag.length-1].trim());
		
		Tag t = null;
		if(query.count()==0){
			t = addTag(name);
		}else{
			if(query.count()>1){
				Tag parent = Tag.filter("name", tag[tag.length-2].trim()).first();
				t = query.filter("context", parent).first();
			}else{
				t = query.first();
			}
			
		}
		return t;
	}
	
	public static Tag addTag(String tagName){
		String[] tag = tagName.split(":");
		Tag t = null;
		for(int i=0;i<tag.length ;i++){
			if(Tag.filter("name", tag[i].trim()).first()== null){
				if(i==0){
					 t  = new Tag(tag[i].trim(),null);
					 t.index=0;
					 t.save();
				}else{
					Tag context = Tag.filter("name", tag[i-1]).first();
					Long index = Tag.filter("context", context).countAll();
					t = new Tag(tag[i].trim(),context);
					t.index = Integer.parseInt(index.toString())+1;
					t.save();
				}
				
			}
		}
		return t;
	}
	
	public static Tag addTag(String tagName,int index){
		String[] tag = tagName.split(":");
		Tag t = null;
		for(int i=0;i<tag.length ;i++){
			if(Tag.filter("name", tag[i].trim()).first()== null){
				if(i==0){
					 t  = new Tag(tag[i].trim(),null);
					 t.index=index;
					 t.save();
				}else{
					Tag context = Tag.filter("name", tag[i-1].trim()).first();
					t = new Tag(tag[i].trim(),context);
					t.index = index;
					t.save();
				}
				
			}
		}
		return t;
	}
	
	public static boolean isExist(String tag){
		if(StringUtils.isBlank(tag)){
			return false;
		}
		String[] tags = tag.split(":");
		MorphiaQuery query = Tag.filter("name", tags[tags.length-1].trim());
		if(query.count() >0){
			return true;
		}
		return false;
	}
	
	public static String getFullTagName(Tag tag){
		String name=tag.name;
		while(tag.context!=null){
			tag = tag.context;
			name = tag.name+":"+name;
		}
		return name;
	}
	
	/**
	 * 使用递归算法来生成所需要的Tree.
	 * @param roots 根节点
	 * @param result 生成tree所需要的JSON串
	 * @param isLinked 点击树是否跳转相应的链接
	 * @param noShowInManager 是否在菜单管理中显示
	 */
	@Util
	public static void createTree(final List<Tag> roots,
			final List<Map> result) {
		for (Tag root:roots) {
			Map node = new HashMap();
			node.put("id", root.getId().toString());
			node.put("text", root.name);
			List<Tag> children = Tag.filter("context", root).order("index").asList();
			List<Map> treeChildren = new ArrayList();
			createTree(children, treeChildren);
			node.put("children", treeChildren);
			result.add(node);
		}
	}
	
	/**
	 * 递归删除树.
	 * @param menueId 树的父亲节点ID
	 */
	public static void delTree(final String tagId) {
		Tag tag = Tag.findById(tagId);
		List<Tag> children = Tag.filter("context", tag).asList();
		for (Tag child:children) {
			delTree(child.getId().toString());
		}
		tag.delete();
		
	}
}
