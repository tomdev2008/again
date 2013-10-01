package controllers.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang.StringUtils;

import models.City;
import models.Course;
import models.Option;
import models.Paper;
import models.Subject;
import models.SubjectSource;
import models.Tag;
import models.enums.SubjectStatus;
import models.enums.SubjectType;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Controller;
import service.CityService;
import service.EasyUIDataGridService;
import service.SubjectSourceService;
import service.TagService;
import service.UserService;


public class Subjects extends  Controller{

	public static void index() {
		render();
    }
	
	public static void list(final int page, final int rows, final String order,
			final String sort, final String keyword) {
		MorphiaQuery query = Subject.find();
		Map result = EasyUIDataGridService.getDataGridMap(page, rows, sort, order, query);
		renderJSON(result, new play.modules.morphia.utils.ObjectIdGsonAdapter());
    }
	
	public static void detail(String id){
		Subject item = Subject.findById(id);
		render(item);
	}
	
	public static void add() {
        render();
    }
	
	public static void create(String title,String subjectType,String course,String grade,String tags,String solution) {
		
		String[] options = params.getAll("option");
		String[] answer = params.getAll("isAnswer");
		Subject sb = new Subject();
		sb.title =title;
		int cnt =1;
		for(String option : options){
			Option o = new Option();
			o.content=option;
			o.save();
			sb.options.add(o);
			for(String t :answer ){
				if(cnt == Integer.valueOf(t)){
					sb.answer.add(o);
				}
			}
			cnt++;
		}
		String[] tagArr = tags.split(",");
		for(String tag:tagArr){
			Tag t = TagService.getTag(tag);
			sb.tags.add(t);
		}
		
		sb.course =Course.filter("name", course).first();

		sb.owner = UserService.getUser("admin");
		sb.status = SubjectStatus.VALID;
		sb.type = SubjectType.valueOf(subjectType);
		sb.solution = solution;
		sb.save();
		index();
    }
	
	public static void update(final String id){
		Subject item = Subject.findById(id);
		render(item);
	}
	
	public static void modify(String subjectId, String title,String subjectType,String course,String grade,String tags,String solution, String removeTagIds, String removeOptionIds) {
		Subject sb = Subject.findById(subjectId);
		if (StringUtils.isNotBlank(removeTagIds)) {
			String[] removeTags = removeTagIds.split(",");
			if (removeTags.length > 0) {
				for (String tag : removeTags) {
					Tag t = Tag.findById(tag);
					sb.tags.remove(t);
				}
			}
		}
		if (StringUtils.isNotBlank(removeOptionIds)) {
			String[] removeOptions = removeOptionIds.split(",");
			if (removeOptions.length > 0) {
				for (String option : removeOptions) {
					Option o = Option.findById(option);
					sb.options.remove(o);
				}
			}
		}
		sb.answer.clear();
		String[] options = params.getAll("option");
		String[] answer = params.getAll("isAnswer");
		sb.title =title;
		int cnt =1;
		if(options !=null && options.length >0){
			for(String option : options){
				if(StringUtils.isBlank(option)){
					continue;
				}
				Option o = new Option();
				o.content=option;
				o.save();
				sb.options.add(o);
				
			}
		}
		
		for(Option o :sb.options){
			if (answer != null) {
				for(String t :answer ){
					if(StringUtils.isBlank(t)){
						continue;
					}
					if(cnt == Integer.valueOf(t)){
						sb.answer.add(o);
					}
				}
			}
			cnt++;
		}
		if (StringUtils.isNotBlank(tags)) {
			String[] tagArr = tags.split(",");
			for(String tag:tagArr){
				Tag t = TagService.getTag(tag);
				sb.tags.add(t);
			}
		}
		
		sb.course =Course.filter("name", course).first();
		sb.owner = UserService.getUser("admin");
		sb.status = SubjectStatus.VALID;
		sb.type = SubjectType.valueOf(subjectType);
		sb.solution = solution;
		sb.updateAt = new Date();
		sb.save();
		index();
    }
	
	public static void delete(final String ids){
		Map result = new HashMap();
		if (StringUtils.isBlank(ids)) {
			result.put("error", "请选择要删除的资源");
			renderJSON(result);
		}
		String[] idArray = ids.split(",");
		for (String id:idArray) {
			Subject sl = Subject.findById(id);
			if (sl == null) {
				continue;
			}
			Subject.findById(id).delete();
		}
		result.put("success", "删除成功");
		renderJSON(result);
	}
	
	public static void batchAdd(){
		render();
	}
	public static void excel(File resource) throws BiffException, IOException{
		String fn = resource.getName();
		String[] sbs = fn.split("-");
		String year = sbs[0];
		Course course = Course.find("name", "公务员行测").first();
		SubjectSource source = SubjectSourceService.getSource(sbs[1]);
		String[] areas = sbs[2].split("_");
		Paper paper = new Paper();
		paper.course = course;
		paper.source =source;
		for(String area:areas){
			paper.cities.add(CityService.getCity(StringUtils.trim(area)));
		}		
		paper.year = Integer.parseInt(year);
		paper.save();
	    Workbook rwb = null;
	    Cell cell = null;
	    
	    //创建输入流
	    InputStream stream = new FileInputStream(resource);
	    
	    //获取Excel文件对象
	    rwb = Workbook.getWorkbook(stream);
	    
	    //获取文件的指定工作表 默认的第3个
	    Sheet sheet = rwb.getSheet(1);  
	    Sheet cailiao = rwb.getSheet(1);
	    //行数(表头的目录不需要，从1开始)
	    
	    for(int i=1; i<sheet.getRows(); i++){
	    
	     //创建一个数组 用来存储每一列的值 
	     //列数
	     if(sheet.getColumns() >0){
		     //题目试卷名字,即便来源
	    	 cell = sheet.getCell(0,i);   
	    	 if(StringUtils.isBlank(cell.getContents().trim())){
	    		 break;
	    	 }
	    	 Subject sb = new Subject();
	    	 
	    	 sb.source = source;
	    	 //index
	    	 cell = sheet.getCell(1,i);   
	    	 sb.index = Integer.parseInt(cell.getContents().trim());
	    	 //title
	    	 cell = sheet.getCell(2,i);
		     sb.title = cell.getContents().trim();
		     //题目类型
		     cell = sheet.getCell(3,i);
		     sb.type = SubjectType.valueOf(cell.getContents().trim());
		     //bigTag
		     cell = sheet.getCell(4,i);  
		     Tag tag =TagService.getTag(cell.getContents().trim());
		     sb.bigTag = tag;
		     //tag
		     cell = sheet.getCell(5,i);  
		     tag =TagService.getTag(cell.getContents().trim());
		     sb.tags.add(tag);
		     
		     //解析
		     cell = sheet.getCell(6,i);  
		     String solu = cell.getContents(); 
		     sb.solution = solu;
		     
		      //选项A
		     Option a = new Option();
		     cell = sheet.getCell(7,i);    
		     a.content = processOption(cell.getContents());
		     a.save();
		     sb.options.add(a);
		      //选项B	
		     Option b = new Option();
		     cell = sheet.getCell(8,i);    
		     b.content = processOption(cell.getContents());
		     b.save();
		     sb.options.add(b);
		      //选项C	
		     Option c = new Option();
		     c.content = processOption(cell.getContents());
		     c.content = cell.getContents();
		     c.save();
		     sb.options.add(c);
		      //选项D	
		     Option d = new Option();
		     cell = sheet.getCell(4,i);    
		     d.content = processOption(cell.getContents());
		     d.save();
		     sb.options.add(d);
		     
		      //正确选项
		     cell = sheet.getCell(5,i);  
		     String anw = processAnswer(cell.getContents()); 
		     String[] as = anw.split(",");
		     for(String s:as){
		    	 int index = Integer.parseInt(s);
		    	 sb.answer.add(sb.options.get(index));
		     }
		    
		     
		      //解析
		    
		      //Course
		     sb.course = course;		     
		     sb.save();
		     
	     	}
	     }
	     //保存材料
	     for(int i=1; i<cailiao.getRows(); i++){
	 	    
		     //创建一个数组 用来存储每一列的值 
		     //列数
		     if(cailiao.getColumns() >0){
		    	 Subject sb = new Subject();
		    	 sb.source = source;
		    	 sb.course = course;	
		    	 //index
		    	 cell = sheet.getCell(1,i);   
		    	 sb.index = Integer.parseInt(cell.getContents().trim());
		    	//title
		    	 cell = sheet.getCell(2,i);
			     sb.title = cell.getContents().trim();
			     //题目类型
			     cell = sheet.getCell(3,i);
			     sb.type = SubjectType.valueOf(cell.getContents().trim());
		    	 if(SubjectType.valueOf(StringUtils.trim(cell.getContents())).equals(SubjectType.MATERIAL)){
		    		 sb.index = i;
		    		 
		    	 }else{
		    	     //bigTag
				     cell = sheet.getCell(4,i);  
				     Tag tag =TagService.getTag(cell.getContents().trim());
				     sb.bigTag = tag;
				     //tag
				     cell = sheet.getCell(5,i);  
				     tag =TagService.getTag(cell.getContents().trim());
				     sb.tags.add(tag);
				     
				     //解析
				     cell = sheet.getCell(6,i);  
				     String solu = cell.getContents(); 
				     sb.solution = solu;
				     
				      //选项A
				     Option a = new Option();
				     cell = sheet.getCell(7,i);    
				     a.content = processOption(cell.getContents());
				     a.save();
				     sb.options.add(a);
				      //选项B	
				     Option b = new Option();
				     cell = sheet.getCell(8,i);    
				     b.content = processOption(cell.getContents());
				     b.save();
				     sb.options.add(b);
				      //选项C	
				     Option c = new Option();
				     c.content = processOption(cell.getContents());
				     c.content = cell.getContents();
				     c.save();
				     sb.options.add(c);
				      //选项D	
				     Option d = new Option();
				     cell = sheet.getCell(4,i);    
				     d.content = processOption(cell.getContents());
				     d.save();
				     sb.options.add(d);
				     
				      //正确选项
				     cell = sheet.getCell(5,i);  
				     String anw = processAnswer(cell.getContents()); 
				     String[] as = anw.split(",");
				     for(String s:as){
				    	 int index = Integer.parseInt(s);
				    	 sb.answer.add(sb.options.get(index));
				     }
		    	 }    
			     sb.save();
		     } 
	    }
	    batchAdd();
	}
	
	public static String processOption(String op){
		op = StringUtils.trim(op);
		if(op.indexOf("A.") >=0 ||
				op.indexOf("B.") >=0 ||
				op.indexOf("C.") >=0 ||
				op.indexOf("D.") >=0){
			op = op.substring(2);
		}
		return op;
	}
	
	public static String  processAnswer(String an ){
		String a = "";
		if(an.indexOf("A") >=0){
			a =a+"0,";
		}
		if(an.indexOf("B") >=0){
			a =a+"1,";
		}
		if(an.indexOf("C") >=0){
			a =a+"2,";
		}
		if(an.indexOf("D") >=0){
			a =a+"3,";
		}
		return a.substring(0,a.length()-1);
	}
}
