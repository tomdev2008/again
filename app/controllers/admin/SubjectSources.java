package controllers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.SubjectSource;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;
import service.CommonTreeService;
import utils.EasyMap;

public class SubjectSources extends Controller {
	/**
	 * 知识点管理.
	 */
	public static void index () {
		render();
	}
	
	public static void tree(){
		List<SubjectSource> roots = SubjectSource.filter("context exists", false).order("index").asList();
		List<Map> result = new ArrayList();
		CommonTreeService.createTree(roots, result);
		renderJSON(result);
	}
	
	/**
	 * 新增一个菜单.
	 * @param SubjectSourceName 菜单名称
	 * @param url 菜单指向URL
	 * @param parentSubjectSourceId 父菜单ID
	 */
	public static void add(final String name, final String contextTagId,final int index) {
		List<SubjectSource> SubjectSources = SubjectSource.filter("name", name).asList();
		if (SubjectSources.size() > 0) {
			renderJSON(new EasyMap("error", "该SubjectSource名已存在，请重新填写！"));
		}
		if (StringUtils.isBlank(name)) {
			renderJSON(new EasyMap("error", "请输入SubjectSource名！"));
		}
 		
 		SubjectSource context = null;
		
	
		
		if (StringUtils.isNotBlank(contextTagId)) {
			SubjectSource parent = SubjectSource.findById(contextTagId);
			if (parent != null) {
				context = parent;
			}
		}
		SubjectSource SubjectSource = new SubjectSource(name,context);
		SubjectSource.index = index;
		SubjectSource.save();
		renderJSON(new EasyMap("success", "新增成功"));
	}
	/**
	 * 删除一个菜单及其子菜单.
	 * @param SubjectSourceId 菜单ID
	 */
	public static void del(final String tagId) {
		if (StringUtils.isBlank(tagId)) {
			renderJSON(new EasyMap("error", "请选择要删除的菜单"));
		}
		if (SubjectSource.findById(tagId) == null) {
			renderJSON(new EasyMap("error", "请选择要删除的菜单"));
		}
		SubjectSource subjectSource =SubjectSource.findById(tagId);
		CommonTreeService.delTree(subjectSource);
		renderJSON(new EasyMap("success", "删除成功"));
	}
	/**
	 * 根据一个菜单ID 返回菜单详情.
	 * @param SubjectSourceId
	 */
	public static void detail(final String tagId) {
		if (StringUtils.isBlank(tagId)) {
			renderJSON(new EasyMap("error", "请选择要修改的菜单"));
		}
		SubjectSource subjectSource =SubjectSource.findById(tagId);
		if (subjectSource == null) {
			renderJSON(new EasyMap("error", "请选择要修改的菜单"));
		}
		renderJSON(subjectSource, new play.modules.morphia.utils.ObjectIdGsonAdapter());
	}
	/**
	 * 修改一个菜单.
	 * @param SubjectSourceId SubjectSourceId
	 */
	public static void update(final String tagId, final String name, final int index) {
		if (StringUtils.isBlank(tagId)) {
			renderJSON(new EasyMap("error", "请选择要更新的菜单"));
		}
		if (StringUtils.isBlank(name)) {
			renderJSON(new EasyMap("error", "请输入菜单名！"));
		}
		SubjectSource subjectSource =SubjectSource.findById(tagId);
		if (subjectSource == null) {
			renderJSON(new EasyMap("error", "请选择要更新的菜单"));
		}
		subjectSource.name = name;
		subjectSource.index = index;
		subjectSource.save();
		renderJSON(new EasyMap("success", "更新成功"));
	}
}
