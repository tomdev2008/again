package controllers.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;
import models.Course;
import models.Option;
import models.Subject;
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
	public static void add(final String name,final String course, final String contextTagId,final int index) {
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
		Course c = Course.filter("name", course).first();
		Tag tag = new Tag(name,context);
		tag.index = index;
		tag.course = c;
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
	public static void update(final String tagId,final String course,final String name, final int index) {
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
		Course c = Course.filter("name", course).first();
		
		tag.course = c;
		tag.name = name;
		tag.index = index;
		tag.save();
		renderJSON(new EasyMap("success", "更新成功"));
	}

	public static void excel(File resource) throws BiffException, IOException{
		
	    Workbook rwb = null;
	    Cell cell = null;
	    
	    //创建输入流
	    InputStream stream = new FileInputStream(resource);
	    
	    //获取Excel文件对象
	    rwb = Workbook.getWorkbook(stream);
	    
	    //获取文件的指定工作表 默认的第3个
	    Sheet sheet = rwb.getSheet(0);  
	   
	    //行数(表头的目录不需要，从1开始)
	    for(int i=1; i<sheet.getRows(); i++){
	    
	     //创建一个数组 用来存储每一列的值 
	     //列数
	     if(sheet.getColumns() >0){
		      //题目
	    	 cell = sheet.getCell(0,i);   
	    	 if(StringUtils.isBlank(cell.getContents())){
	    		 break;
	    	 }
	    	 String courseName = StringUtils.trim(cell.getContents());
	    	 Course course = Course.filter("name",courseName ).first();
	    	 if(course == null){
	    		 course = new Course(courseName,null);
	    		 course.save();
	    	 }
		      //知识点
		     Tag parent= null;
		     for(int j=1;j<sheet.getColumns();j++){
		    	 cell = sheet.getCell(j,i);    
			     String tagName = StringUtils.trim(cell.getContents());
			     if(StringUtils.isBlank(tagName)){
			    	 break;
			     }
			     Tag tag = Tag.filter("name", tagName).first();
			     if(tag == null){
			    	 tag = new Tag();
			    	 tag.course =course;
			    	 tag.name=tagName;
			    	 tag.context = parent;
			    	 int cnt =0;
			    	 if(parent == null){
			    		 cnt =(int)Tag.filter("context exists", false).count();
			    	 }else{
			    		 cnt = (int)Tag.filter("context", parent).count();
			    	 }
			    	 tag.index =cnt;
			    	 tag.save();
			     }
			     parent = tag;
		     }
	     }
	    }
	    index();
	}
}
