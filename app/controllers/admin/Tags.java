package controllers.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import models.Menu;
import models.Subject;
import models.Tag;

import org.apache.commons.lang.StringUtils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;

import play.Logger;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Controller;
import play.mvc.Util;
import service.EasyUIDataGridService;
import service.TagService;
import service.TreeService;
import service.UserService;


public class Tags extends Controller {

	public static void index() {
		render();
    }
	

	public static void list(final int page, final int rows, final String order,
			final String sort, final String keyword) {
		MorphiaQuery query = Tag.find();
		Map result = EasyUIDataGridService.getDataGridMap(page, rows, sort, order, query);
		renderJSON(result, new play.modules.morphia.utils.ObjectIdGsonAdapter());
    }
	
	public static void add() {
        render();
    }
	
	public static void create(String tagName,String isImportant) {
		
		if(!TagService.isExist(tagName)){
			Tag t =TagService.addTag(tagName);
			if(StringUtils.isNotBlank(isImportant)){
				t.info.put("isImportant", "1");
				t.save();
			}
		}
		index();
		
    }
	
	public static void update(final String id){
		render();
	}

	public static void delete(final String ids){
		Map result = new HashMap();
		if (StringUtils.isBlank(ids)) {
			result.put("error", "请选择要删除的资源");
			renderJSON(result);
		}
		String[] idArray = ids.split(",");
		for (String id:idArray) {
			Tag sl = Tag.findById(id);
			if (sl == null) {
				continue;
			}
			if(Subject.filter("tags", sl).countAll() >0){
				Subject sb = Subject.filter("tags", sl).first();
				result.put("error", "有题目引用该tag，请先修改题目的tag:"+sb.getId()+","+sb.title);
				renderJSON(result);
			}
			if(Subject.filter("course", sl).countAll() >0){
				Subject sb = Subject.filter("course", sl).first();
				result.put("error", "有题目引用该course，请先修改题目的course:"+sb.getId()+","+sb.title);
				renderJSON(result);
			}
			if(Subject.filter("grade", sl).countAll() >0){
				Subject sb = Subject.filter("grade", sl).first();
				result.put("error", "有题目引用该grade，请先修改题目的grade:"+sb.getId()+","+sb.title);
				renderJSON(result);
			}
			Tag.findById(id).delete();
		}
		result.put("success", "删除成功");
		renderJSON(result);
	}

}
