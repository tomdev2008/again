package controllers;

import java.util.ArrayList;
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
import service.UserTagService;
@With(Secure.class)
public class Application extends Controller {

    public static void index(String courseName) {
    	if(StringUtils.isBlank(courseName)){
    		courseName = "公务员行测";
    	}
    	User user = Secure.getLoginUser();
    	Course course = Course.filter("name", courseName).first();
  
    	UserCourse uc = UserCourse.find("user", user).filter("course.id", course.id).first();
    	List<UserTag> roots = UserTag.find("user", user).filter("context exists", false).filter("course.id", course.id).order("tag.index").asList();
    	List<Map> result = new ArrayList();
    	UserTagService.createTree(roots, result);
    	
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