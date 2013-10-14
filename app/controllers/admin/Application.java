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

import job.UserSubjectInitJob;
import jxl.read.biff.BiffException;
import models.City;
import models.Course;
import models.Menu;
import models.Subject;
import models.SubjectSource;
import models.Tag;
import models.User;
import models.UserExercise;
import models.enums.RoleType;
import controllers.Public;
import controllers.wither.LogPrinter;
import play.Play;
import play.libs.Codec;
import play.modules.morphia.Model;
import play.mvc.Controller;
import play.mvc.With;
import service.TagService;
import service.TreeService;
import utils.YtkImportTools;

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
	
	
	public static void importYTK() throws IOException{
		File dir =Play.getFile("ytk");
		if(dir.exists() && dir.isDirectory()){
			for(File f :dir.listFiles()){
				try {
					YtkImportTools.importFile(f);
				} catch (BiffException e) {
					System.out.println("file is fail:"+f.getName());
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void initTest(){
		User user = User.find("userName", "admin").first();
		Course course =Course.find("name", "公务员行测").first();
		SubjectSource source = SubjectSource.find("name","上海市公务员录用考试《行测》真题").first();
		City city = City.filter("name", "上海").first();
		
		
		new UserSubjectInitJob(user, city, course, source).now();
	}
	
	public static void getData(){
		
		Tag tag = Tag.find("name", "判断推理").first();
		Set<Tag> tags  = new HashSet<Tag>();
		tags.add(tag);
		List<Subject> list = Subject.find("tags.id", tag.id).asList();
		for(Subject sb:list){
			System.out.println(sb.id);
		}
	}
	
	
}