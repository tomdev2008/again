package controllers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;

import models.Tag;
import service.TagService;
import utils.EasyMap;

public class KnowlegeTags extends Controller {
	/**
	 * 知识点管理.
	 */
	public static void index () {
		render();
	}
	
	public static void tree(){
		List<Tag> roots = Tag.filter("context exists", false).order("index").asList();
		List<Map> result = new ArrayList();
		TagService.createTree(roots, result);
		renderJSON(result);
	}
	
	/**
	 * 新增一个菜单.
	 * @param TagName 菜单名称
	 * @param url 菜单指向URL
	 * @param parenttagId 父菜单ID
	 */
	public static void add(final String name, final String contextTagId,final int index) {
		List<Tag> Tags = Tag.filter("name", name).asList();
		if (Tags.size() > 0) {
			renderJSON(new EasyMap("error", "该tag名已存在，请重新填写！"));
		}
		if (StringUtils.isBlank(name)) {
			renderJSON(new EasyMap("error", "请输入tag名！"));
		}
 		
 		Tag context = null;
		
	
		
		if (StringUtils.isNotBlank(contextTagId)) {
			Tag parent = Tag.findById(contextTagId);
			if (parent != null) {
				context = parent;
			}
		}
		Tag tag = new Tag(name,context);
		tag.index = index;
		tag.save();
		renderJSON(new EasyMap("success", "新增成功"));
	}
	/**
	 * 删除一个菜单及其子菜单.
	 * @param TagId 菜单ID
	 */
	public static void del(final String tagId) {
		if (StringUtils.isBlank(tagId)) {
			renderJSON(new EasyMap("error", "请选择要删除的菜单"));
		}
		if (Tag.findById(tagId) == null) {
			renderJSON(new EasyMap("error", "请选择要删除的菜单"));
		}

		TagService.delTree(tagId);
		renderJSON(new EasyMap("success", "删除成功"));
	}
	/**
	 * 根据一个菜单ID 返回菜单详情.
	 * @param tagId
	 */
	public static void detail(final String tagId) {
		if (StringUtils.isBlank(tagId)) {
			renderJSON(new EasyMap("error", "请选择要修改的菜单"));
		}
		Tag tag = Tag.findById(tagId);
		if (tag == null) {
			renderJSON(new EasyMap("error", "请选择要修改的菜单"));
		}
		renderJSON(tag, new play.modules.morphia.utils.ObjectIdGsonAdapter());
	}
	/**
	 * 修改一个菜单.
	 * @param tagId tagId
	 */
	public static void update(final String tagId, final String name, final int index) {
		if (StringUtils.isBlank(tagId)) {
			renderJSON(new EasyMap("error", "请选择要更新的菜单"));
		}
		if (StringUtils.isBlank(name)) {
			renderJSON(new EasyMap("error", "请输入菜单名！"));
		}
		Tag tag = Tag.findById(tagId);
		if (tag == null) {
			renderJSON(new EasyMap("error", "请选择要更新的菜单"));
		}
		tag.name = name;
		tag.index = index;
		tag.save();
		renderJSON(new EasyMap("success", "更新成功"));
	}
}
