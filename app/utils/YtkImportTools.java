package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import models.Course;
import models.Option;
import models.Paper;
import models.Subject;
import models.SubjectSource;
import models.Tag;
import models.enums.SubjectStatus;
import models.enums.SubjectType;

import org.apache.commons.lang.StringUtils;

import service.CityService;
import service.SubjectSourceService;
import service.TagService;

public class YtkImportTools {

	
	public static void importFile(File resource) throws BiffException, IOException{
		String fn = resource.getName();
		System.out.println("==>"+fn);
		String[] sbs = fn.split("-");
		String year = sbs[0];
		Course course = Course.find("name", "公务员行测").first();
		if(course == null){
			course = new Course("公务员行测",null);
			course.save();
			
		}
		SubjectSource source = SubjectSourceService.getSource(sbs[1]);
		String[] areas = sbs[2].split("_");
		
		Paper paper = Paper.filter("source", source).first();
		if(paper == null){
			paper = new Paper();
			paper.course = course;
			paper.source =source;
			for(String area:areas){
				String temp = StringUtils.trim(area);
				if(temp.indexOf(".")>=0){
					temp = temp.substring(0,temp.indexOf("."));
				}
				paper.cities.add(CityService.getCity(temp));
			}		
			paper.year = Integer.parseInt(year);
			//paper.save();
		}
		
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
	    	System.out.println("row==>"+i);
	     //创建一个数组 用来存储每一列的值 
	     //列数
	     if(sheet.getColumns() >0){
		     //题目试卷名字,即便来源
	    	 cell = sheet.getCell(0,i);   
	    	 if(StringUtils.isBlank(cell.getContents().trim())){
	    		 continue;
	    	 }
	    	 Subject sb = new Subject();
	    	 sb.cities = paper.cities;
	    	 sb.year = paper.year;
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
		     Integer cnt =  paper.bigTag.get(tag);
		     if(cnt == null){
		    	 cnt = new Integer(0);
		     }
		     cnt = cnt.intValue()+1;
		     paper.bigTag.put(tag, cnt);
		     //tag
		     cell = sheet.getCell(5,i);  
		     tag =TagService.getTag(cell.getContents().trim());
		     sb.tags.add(tag);
		     paper.tags.add(tag);
		     //解析
		     cell = sheet.getCell(7,i);  
		     String solu = cell.getContents(); 
		     sb.solution = solu;
		     int col =8;
		    
		     while(col <  sheet.getColumns()){
		    	 if(StringUtils.isBlank(cell.getContents())){
		    		 break;
		    	 }
		    	  //选项A
			     Option a = new Option();
			     cell = sheet.getCell(col,i);    
			     String content =  processOption(cell.getContents());
			     if(content.equals("")){
			    	 continue;
			     }
			     a.content =content;
			     a.save();
			     sb.options.add(a);
			     col++;
		     }
		  
//		      //选项A
//		     Option a = new Option();
//		     cell = sheet.getCell(8,i);    
//		     a.content = processOption(cell.getContents());
//		     a.save();
//		     sb.options.add(a);
//		      //选项B	
//		     Option b = new Option();
//		     cell = sheet.getCell(9,i);    
//		     b.content = processOption(cell.getContents());
//		     b.save();
//		     sb.options.add(b);
//		      //选项C	
//		     Option c = new Option();
//		     cell = sheet.getCell(10,i);   
//		     c.content = processOption(cell.getContents());
//		     c.content = cell.getContents();
//		     c.save();
//		     sb.options.add(c);
//		      //选项D	
//		     Option d = new Option();
//		     cell = sheet.getCell(11,i);    
//		     d.content = processOption(cell.getContents());
//		     d.save();
//		     sb.options.add(d);
		     
	     	//正确选项
		     cell = sheet.getCell(6,i);  
		     String anw = processAnswer(cell.getContents()); 
		     String[] as = anw.split(",");
		     if(i==61){
		    	 System.out.println("comming");
		     }
		     if(sb.options.size() ==0){
		    	 sb.status = SubjectStatus.VALID;
		     }else{
			     for(String s:as){
			    	 int index = Integer.parseInt(s);
			    	 sb.answer.add(sb.options.get(index));
			     }
		     }
		     
		      //解析
		    
		      //Course
		     sb.course = course;		     
		     sb.save();
		     
	     	}
	     }
	    sheet = rwb.getSheet(1);
	     //保存材料
	    Subject big = null;
	     for(int i=1; i<sheet.getRows(); i++){
	 	    
		     //创建一个数组 用来存储每一列的值 
		     //列数
	    	 
		     if(sheet.getColumns() >0){

		    	 Subject sb = new Subject();
		    	 sb.cities = paper.cities;
		    	 sb.year = paper.year;
		    	 sb.source = source;
		    	 sb.course = course;	
		    	 //index
		    	 cell = sheet.getCell(1,i);   
		    	 if(StringUtils.isBlank(cell.getContents().trim())){
		    		 continue;
		    	 }
		    	 sb.index = Integer.parseInt(cell.getContents().trim());
		    	//title
		    	 cell = sheet.getCell(2,i);
			     sb.title = cell.getContents().trim();
			     //题目类型
			     cell = sheet.getCell(3,i);
			     sb.type = SubjectType.valueOf(cell.getContents().trim());
		    	 if(SubjectType.valueOf(StringUtils.trim(cell.getContents())).equals(SubjectType.MATERIAL)){
		    		 sb.index = i;
		    		 sb.save();
		    		 big  = sb;
		    	 }else{
		    	     //bigTag
				     cell = sheet.getCell(4,i);  
				     Tag tag =TagService.getTag(cell.getContents().trim());
				     sb.bigTag = tag;
				     Integer cnt =  paper.bigTag.get(tag);
				     if(cnt == null){
				    	 cnt = new Integer(0);
				     }
				     cnt = cnt.intValue()+1;
				     paper.bigTag.put(tag, cnt);
				     //tag
				     cell = sheet.getCell(5,i);  
				     tag =TagService.getTag(cell.getContents().trim());
				     sb.tags.add(tag);
				     paper.tags.add(tag);
				    
				     //解析
				     cell = sheet.getCell(7,i);  
				     String solu = cell.getContents(); 
				     sb.solution = solu;
				     
				     int col =8;
				     while(col <  sheet.getColumns()){
				    	 if(StringUtils.isBlank(cell.getContents())){
				    		 break;
				    	 }
				    	  //选项A
					     Option a = new Option();
					     cell = sheet.getCell(col,i); 
					     String content =  processOption(cell.getContents());
					     if(content.equals("")){
					    	 continue;
					     }
					     a.content =content;
					     a.save();
					     sb.options.add(a);
					     col++;
				     }
				     
//				      //选项A
//				     Option a = new Option();
//				     cell = sheet.getCell(8,i);    
//				     a.content = processOption(cell.getContents());
//				     a.save();
//				     sb.options.add(a);
//				      //选项B	
//				     Option b = new Option();
//				     cell = sheet.getCell(9,i);    
//				     b.content = processOption(cell.getContents());
//				     b.save();
//				     sb.options.add(b);
//				      //选项C	
//				     Option c = new Option();
//				     cell = sheet.getCell(10,i);   
//				     c.content = processOption(cell.getContents());
//				     c.save();
//				     sb.options.add(c);
//				      //选项D	
//				     Option d = new Option();
//				     cell = sheet.getCell(11,i);    
//				     d.content = processOption(cell.getContents());
//				     d.save();
//				     sb.options.add(d);
				     //正确选项
				     cell = sheet.getCell(6,i);  
				     String anw = processAnswer(cell.getContents()); 
				     String[] as = anw.split(",");
				     if(sb.options.size() ==0){
				    	 sb.status = SubjectStatus.VALID;
				     }else{
					     for(String s:as){
					    	 int index = Integer.parseInt(s);
					    	 sb.answer.add(sb.options.get(index));
					     }
				     }
				     sb.save();
				     big.bigTag = sb.bigTag;
				     big.tags.add(tag);
				     big.subs.add(sb);
				     big.index = sb.index;
				     big.save();
		    	 } 
		     } 
	    }
	     paper.save();
	}
	
	public static String processOption(String op){
		op = StringUtils.trim(op);
		if(StringUtils.isBlank(op)){
			return "";
		}
		if(op.indexOf("A.") >=0 ||
				op.indexOf("B.") >=0 ||
				op.indexOf("C.") >=0 ||
				op.indexOf("D.") >=0){
			op = op.substring(2);
		}
		return op;
	}
	
	public static String  processAnswer(String an ){
		if(StringUtils.isBlank(an)){
			return "";
		}
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
		if(an.indexOf("E") >=0){
			a =a+"4,";
		}
		if(an.indexOf("F") >=0){
			a =a+"5,";
		}
		if(an.indexOf("G") >=0){
			a =a+"6,";
		}
		return a.substring(0,a.length()-1);
	}
}
