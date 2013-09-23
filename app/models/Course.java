package models;

import java.util.HashMap;
import java.util.Map;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;
import tools.SequenceUtils;

@Entity
public class Course extends BaseModel{

	@Reference
	public Course context;

	
	public String name;
	
	public int index;
	
	public Course(String name,Course tagContext){
		this.name=name;
		this.context=tagContext;
		
	}
	
	
	public String getFullTagName(){
		String tName = this.name;
		Course tag = this.context;
		while(tag !=null){
			tName = tag.name+":"+tName;
			tag = tag.context;
		}
		return tName;
	}
}
