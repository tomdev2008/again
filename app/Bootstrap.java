import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jxl.read.biff.BiffException;
import controllers.admin.KnowlegeTags;
import models.AdminRole;
import models.User;
import models.Menu;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.cache.EhCacheImpl;
import play.jobs.Job;
import play.jobs.OnApplicationStart;


/**
 * Application Startup
 * @author jiwei
 * @since 2013-7-14
 */
@OnApplicationStart
public class Bootstrap extends Job {

    @Override
    public void doJob() {       
        initRoleUserMenu();

    }

    /**
     * 初始化菜单、超级管理员角色、以及超级管理员用户.
     */
    private static void initRoleUserMenu() {
        List<Menu> menuList = Menu.findAll();
        Set<Menu> menus = new HashSet<Menu>(menuList);
    	if (menus.size() == 0) {
    		Menu menu = new Menu();
        	menu.menuName = "管理中心";
        	menu.save();
        	Menu menu1 = new Menu();
        	menu1.menuName = "系统管理";
        	menu1.parent = menu;
        	menu1.save();
        	Menu menu2 = new Menu();
        	menu2.menuName = "菜单管理";
        	menu2.url = "/admin/menus/menuManager";
        	menu2.parent = menu1;
        	menu2.save();
        	Menu menu3 = new Menu();
        	menu3.menuName = "角色管理";
        	menu3.url = "/admin/roles/roleManager";
        	menu3.parent = menu1;
        	menu3.save();
        	Menu menu4 = new Menu();
        	menu4.menuName = "用户管理";
        	menu4.url = "/admin/users/userManager";
        	menu4.parent = menu1;
        	menu4.save();
        	Menu menu5 = new Menu();
        	menu5.url = "/admin.Subjects/index";
        	menu5.menuName = "试题管理";
        	menu5.parent = menu;
        	menu5.save();
        	Menu menu6 = new Menu();
        	menu6.url = "/admin.Subjects/batchAdd";
        	menu6.menuName = "试题批量导入";
        	menu6.parent = menu;
        	menu6.save();
        	Menu menu7 = new Menu();
        	menu7.url = "/admin.KnowlegeTags/index";
        	menu7.menuName = "知识点管理";
        	menu7.parent = menu;
        	menu7.save();
        	Menu menu8 = new Menu();
        	menu8.url = "/admin.Courses/index";
        	menu8.menuName = "课程管理";
        	menu8.parent = menu;
        	menu8.save();
        	Menu menu9 = new Menu();
        	menu9.url = "/admin.SubjectSources/index";
        	menu9.menuName = "来源管理";
        	menu9.parent = menu;
        	menu9.save();
        	menus.add(menu1);menus.add(menu2);menus.add(menu3);menus.add(menu4);
        	//menus.add(menu5);menus.add(menu6);
//        	menus.add(menu8);menus.add(menu9);menus.add(menu10);menus.add(menu11);menus.add(menu12);menus.add(menu13);menus.add(menu14);
    	}
        if (User.count() == 0) {
            AdminRole superRole = new AdminRole();
            superRole.menuList = menus;
            superRole.roleLevel = 100;
            superRole.roleName = "超级管理员";
            superRole.roleDesc = "超级管理员权限，拥有一切";
            superRole.save();
            User superUser = new User("admin", "123456", superRole);
            superUser.save(); 
        }
        initTag();
    }
    
    public static void initCourse(){

    }
    
    public static void initTag(){
    	File resource = Play.getFile("tags/gwy.xls");
    	try {
			KnowlegeTags.importExcel(resource);
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void initSource(){

    }
    
}
