package utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

public class Question {

	public int id;
	
	public String type;
	
	public String title;
	
	public List<String> options = new ArrayList<String>();
	
	public String source;
	
	public String year;
	
	public String area;
	
	public String bigTag;
	
	public String tags = new String();
	
	public String ans ;
	
	public String  solution;
	
	public List<Question> subs = new ArrayList<Question>(); 
	
	public void setType(String typeSrt){
		if("(单选题)".equals(typeSrt)){
			this.type ="SINGLE";
		}else{
			this.type ="MUTI";
		}
//		else if("(多选题)".equals(typeSrt)){
//			this.type ="MUTI";
//		}
	}
	
	public void setSubType(String typeSrt){
		if("(单选题)".equals(typeSrt)){
			type ="SUBS";
		}else{
			type ="SUBM";
		}
//		else if("(多选题)".equals(typeSrt)){
//			type ="SubM";
//		}
	}
	
	public void setMType(){
		type ="Material";
	}
		
	public void setIndex(String indexStr){
		id = NumberUtils.toInt(indexStr);
	}
	
	public void setTag(String tags){
		this.tags = tags.replace("考点,", "");
		
	}
}
