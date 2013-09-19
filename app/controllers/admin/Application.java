package controllers.admin;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Menu;
import models.Subject;
import models.Tag;
import models.User;
import models.UserExercise;
import models.enums.RoleType;
import models.enums.UserExerciseStatus;
import controllers.Public;
import controllers.wither.LogPrinter;
import play.Play;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;
import service.TagService;
import service.TreeService;

/**
 * 后台运营管理系统页面控制器.
 * @author jiwei
 * @since 2013-7-14
 */
@With({Secure.class, LogPrinter.class })
public class Application extends Controller {
	/**
	 * <p>首页导航页面.</p>
	 */
    public static void index() {
        render();
    }
    /**
	 * <p>首页登陆页面.</p>
	 */
    public static void login() {
        render();
    }
    /**
     * <p>首页显示的欢迎页面.</p>
     */
    @Public
    public static void welcome() {
    	render();
    }
    
	/**
	 * 生成首页菜单树.
	 */
	public static void tree() {
		List<Menu> roots = Menu.filter("parent exists", false).order("order").asList();
		List<Map> result = new ArrayList();
		TreeService.createTree(roots, result, true);
		renderJSON(result);
	}
	
	
	public static void initTag() throws IOException{
		
		
		File file = Play.getFile("/tags/english-1.txt");
		FileReader reader = new FileReader(file);
		BufferedReader br = new BufferedReader(reader);
		String tag = null;
		while((tag =br.readLine()) != null) {
			TagService.addTag(tag);
		}
		br.close();
		reader.close();
		
		file = Play.getFile("/tags/grade-1.txt");
		reader = new FileReader(file);
		br = new BufferedReader(reader);
		
		while((tag =br.readLine()) != null) {
			TagService.addTag(tag);
		}
		br.close();
		reader.close();
		
		file = Play.getFile("/tags/math-1.txt");
		reader = new FileReader(file);
		br = new BufferedReader(reader);
		
		while((tag =br.readLine()) != null) {
			TagService.addTag(tag);
		}
		br.close();
		reader.close();
		
		file = Play.getFile("/tags/origin-1.txt");
		reader = new FileReader(file);
		br = new BufferedReader(reader);
		
		while((tag =br.readLine()) != null) {
			TagService.addTag(tag);
		}
		br.close();
		reader.close();
		
		
		file = Play.getFile("/tags/course-1.txt");
		reader = new FileReader(file);
		br = new BufferedReader(reader);
		
		while((tag =br.readLine()) != null) {
			TagService.addTag(tag);
		}
		br.close();
		reader.close();
		
	}
	
	public static void fixTag(){
		Tag t = TagService.getTag("真题\"");
		t.name="真题";
		t.save();
	}
	public static void initUserExercise(){
		
		User user = User.find("userName", "test1").first();
		if(user == null){
			user = new User("test1","123456");
			user.roleType = RoleType.STUDENT;
			user.save();
		}
		
//		StudentProfile st = StudentProfile.find("user", user).first();
//		if(st == null){
//			st = new StudentProfile();
//			st.user = user;
//			
//			Tag tag1 = Tag.filter("name", "英语").first();
//			Tag tag2 = Tag.filter("name", "数学").first();
//			st.courses.put(tag1,0L);
//			st.courses.put(tag2,0L);
//			Tag tag3 = Tag.filter("name", "高一").first();
//			st.grades.add(tag3);
//			ScheduleInfo sif = new ScheduleInfo();
//			sif.tag  = Tag.filter("name", "元素与集合的概念").first();
//			sif.save();
//			st.schedule.add(sif);
//			st.save();
//		}
//		
//		//Set<Tag> tags = new HashSet();
//		//tags.add((Tag)Tag.filter("name", "数学").first());
//		//tags.add((Tag)Tag.filter("name", "高一").first());
//		//tags.add((Tag)Tag.filter("name", "元素与集合的概念").first());
//		//List<Subject> sbs = Subject.filter("tags", tags).asList();
//		List<Subject> sbs = Subject.findAll();
//		for(Subject sb:sbs){
//			UserExercise ue = new UserExercise();
//			ue.status =UserExerciseStatus.START;
//			ue.user = user;
//			ue.subject = sb;
//			ue.tags = sb.tags;
//			ue.save();
//		}
		
	}
}