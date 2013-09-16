package extensions;

import models.Tag;
import play.templates.JavaExtensions;

public class TagExtensions extends JavaExtensions {

	public static String getFullTagName(Tag tag){
		String name=tag.name;
		while(tag.context!=null){
			tag = tag.context;
			name = tag.name+":"+name;
		}
		return name;
	}
}
