package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import models.Course;
import models.Tag;
import models.User;
import models.UserCourse;
import models.UserTag;
import play.mvc.Controller;
import play.mvc.With;
@With(Secure.class)
public class Application extends Controller {

    public static void index(String courseName) {
    	if(StringUtils.isBlank(courseName)){
    		courseName = "公务员行测";
    	}
    	User user = Secure.getLoginUser();
    	Course course = Course.filter("name", courseName).first();
  
    	UserCourse uc = UserCourse.find("user", user).filter("course.id", course.id).first();
    	List<Tag> bigTags = Tag.filter("course.id", course.id).filter("contextTag is", null).order("index").asList();
    	for(Tag t:bigTags){
    		long cnt = UserTag.filter("user", user).filter("course.id", course.id).filter("bigTag", t).count();
    		
    		Map node = new HashMap();
    		node.put("name", t.name);
    		node.put("", "");
    	}
    	
    	List<Tag> tags = UserTag.filter("user", user).filter("course.id", course.id).asList();
        render(uc,tags);
    }
    public static void reg() {
    	render("reg.html");
    }
    public static void doExercise(String courseName) {
    	render(courseName);
    }
    
    public static void result() {
    	render();
    }
}