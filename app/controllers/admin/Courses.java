package controllers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Course;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;
import service.CommonTreeService;
import utils.EasyMap;

public class Courses extends Controller {
	/**
	 * 知识点管理.
	 */
	public static void index () {
		render();
	}
	
	public static void tree(){
		List<Course> roots = Course.filter("context exists", false).filter("name", "Math").order("index").asList();
		List<Map> result = new ArrayList();
		CommonTreeService.createTree(roots, result);
		roots = Course.filter("context exists", false).filter("name", "English").order("index").asList();
		CommonTreeService.createTree(roots, result);
		renderJSON(result);
	}
	
	/**
	 * 新增一个菜单.
	 * @param CourseName 菜单名称
	 * @param url 菜单指向URL
	 * @param parentCourseId 父菜单ID
	 */
	public static void add(final String name, final String contextCourseId,final int index) {
		List<Course> Courses = Course.filter("name", name).asList();
		if (Courses.size() > 0) {
			renderJSON(new EasyMap("error", "该Course名已存在，请重新填写！"));
		}
		if (StringUtils.isBlank(name)) {
			renderJSON(new EasyMap("error", "请输入Course名！"));
		}
 		
 		Course context = null;
		
	
		
		if (StringUtils.isNotBlank(contextCourseId)) {
			Course parent = Course.findById(contextCourseId);
			if (parent != null) {
				context = parent;
			}
		}
		Course Course = new Course(name,context);
		Course.index = index;
		Course.save();
		renderJSON(new EasyMap("success", "新增成功"));
	}
	/**
	 * 删除一个菜单及其子菜单.
	 * @param CourseId 菜单ID
	 */
	public static void del(final String CourseId) {
		if (StringUtils.isBlank(CourseId)) {
			renderJSON(new EasyMap("error", "请选择要删除的菜单"));
		}
		if (Course.findById(CourseId) == null) {
			renderJSON(new EasyMap("error", "请选择要删除的菜单"));
		}
		Course course =Course.findById(CourseId);
		CommonTreeService.delTree(course);
		renderJSON(new EasyMap("success", "删除成功"));
	}
	/**
	 * 根据一个菜单ID 返回菜单详情.
	 * @param CourseId
	 */
	public static void detail(final String CourseId) {
		if (StringUtils.isBlank(CourseId)) {
			renderJSON(new EasyMap("error", "请选择要修改的菜单"));
		}
		Course course =Course.findById(CourseId);
		if (course == null) {
			renderJSON(new EasyMap("error", "请选择要修改的菜单"));
		}
		renderJSON(course, new play.modules.morphia.utils.ObjectIdGsonAdapter());
	}
	/**
	 * 修改一个菜单.
	 * @param CourseId CourseId
	 */
	public static void update(final String CourseId, final String name, final int index) {
		if (StringUtils.isBlank(CourseId)) {
			renderJSON(new EasyMap("error", "请选择要更新的菜单"));
		}
		if (StringUtils.isBlank(name)) {
			renderJSON(new EasyMap("error", "请输入菜单名！"));
		}
		Course course =Course.findById(CourseId);
		if (course == null) {
			renderJSON(new EasyMap("error", "请选择要更新的菜单"));
		}
		course.name = name;
		course.index = index;
		course.save();
		renderJSON(new EasyMap("success", "更新成功"));
	}
}
