package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;

@Entity
public class SubjectSource extends BaseModel{
	@Reference
	public SubjectSource context;

	public String name;
	
	public int index;
	
	public SubjectSource(String name,SubjectSource tagContext){
		this.name=name;
		this.context=tagContext;
	}
		
	public String getFullTagName(){
		String tName = this.name;
		SubjectSource tag = this.context;
		while(tag !=null){
			tName = tag.name+":"+tName;
			tag = tag.context;
		}
		return tName;
	}
}
