package models;

import java.util.HashMap;
import java.util.Map;

import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

@Entity
public class Tag extends BaseModel{

	@Reference
	public Tag context;
	
	public Course course;
	
	public String name;
	
	public int index;
	
	public int subjectCnt;
	
	public Map<String,String> info = new HashMap<String,String>();
	
	
	public Tag(String name,Tag tagContext){
		this.name=name;
		this.context=tagContext;
	}
	
	
	public Tag() {
	}


	public String getFullTagName(){
		String tName = this.name;
		Tag tag = this.context;
		while(tag !=null){
			tName = tag.name+":"+tName;
			tag = tag.context;
		}
		return tName;
	}

}
