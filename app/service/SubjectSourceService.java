package service;

import models.SubjectSource;

public class SubjectSourceService {

	
	public static SubjectSource getSource(String name){
		
		SubjectSource source = SubjectSource.find("name", name).first();
		if(source == null){
			source = new SubjectSource(name, null);
			source.save();
		}
		return source;
	}
}
