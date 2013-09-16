package controllers;

import models.StudentProfile;
import models.User;
import play.mvc.Controller;
import play.mvc.With;
@With(Secure.class)
public class Students extends Controller {
	public static void index(final String userid) {
		//TODO：获取当前用户
		User user = User.filter("userName", "test1").first();
				
		
		//TODO:获取该学生的当前学科
		StudentProfile stp = StudentProfile.filter("user", user).first();
		//获取每一学科的详细数据
		
		//学科预测分数。 
		
		//历史曲线图，暂时没数据
		//预测分数
		
		//同类考生平均预测分
		
		//已击败同类考生比例
		
		//知识点明细
		render();
	}
}
