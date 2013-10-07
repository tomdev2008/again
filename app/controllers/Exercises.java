package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Course;
import models.Option;
import models.Paper;
import models.Subject;
import models.Tag;
import models.User;
import models.UserCourse;
import models.UserExercise;
import models.UserExerciseItem;
import models.UserSubject;
import models.enums.ExerciseType;
import models.enums.SubjectType;

import org.apache.commons.lang.time.DateUtils;

import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Controller;
import play.mvc.Util;
import play.mvc.With;
import service.TagService;

import com.google.code.morphia.Key;
@With(Secure.class)
public class Exercises extends Controller {

	public static void index(){
		
		User user = Users.getLoginUser();
		List<UserCourse> profile = UserCourse.filter("user", user).asList();
		render(profile);
	}
	
	/****
	 * 快速练习
	 * **/
	public static void quick(String courseName, int num){
		Course course = Course.find("name",courseName).first();
		User user = Users.getLoginUser();
		Date now = new Date(System.currentTimeMillis());
		now = DateUtils.setHours(now, 0);
		now = DateUtils.setMinutes(now, 0);
		now = DateUtils.setMilliseconds(now, 0);
		List<UserSubject> exercises = UserSubject.find("user", user).filter("course", course).asList();
		if(exercises.size() == 0){
			
		}else{
			MorphiaQuery query = UserSubject.find("user", user)
					.filter("course", course)
					.filter("nextDate <=", new Date())
					.filter("URT >", 0)
					.order("-weight");
			if(query.countAll() >num){
				exercises = query.limit(num).asList();
			}else{
				exercises = query.asList();
			}
		}
		UserExercise ue= new UserExercise();
		ue.course =course;
		ue.status =0;
		ue.type =ExerciseType.QUICK;
		
		for(UserSubject sb:exercises){
			UserExerciseItem ueItem = new UserExerciseItem();
			ueItem.subject = sb.subject;
			ueItem.user = user;
			ueItem.save();
			ue.userExerciseItem.add(ueItem);
		}
		ue.save();
		render(ue);
	}
	
	/****
	 * 真题
	 * **/
	public static void exam(long pid){
		User user = Users.getLoginUser();
		Paper paper = Paper.find("id", pid).first();
		SubjectType[] type = {SubjectType.SUBS,SubjectType.SUBM};
		List<Subject> list = Subject.find("source", paper.course).filter("type nin", type).order("index").asList();
		UserExercise ue= new UserExercise();
		ue.course =paper.course;
		ue.status =0;
		ue.type =ExerciseType.ORIGIN;
		ue.user = user;
		for(Subject sb:list){
			UserExerciseItem ueItem = new UserExerciseItem();
			ueItem.subject = sb;
			ueItem.user = user;
			ueItem.save();
			ue.userExerciseItem.add(ueItem);
			
			if(UserSubject.find("user", user).filter("subject", sb).first() == null){
				UserSubject us = new UserSubject();
				us.user = user;
				us.subject = sb;
				us.save();
			}
		}
		ue.save();
		render(ue);
	}
	
	public static void end(long ueid ,String answer,long time){
		User user = Users.getLoginUser();
		String[] an = answer.split(",");
		int correctCnt=0;
		int mistakeCnt=0;
		UserExercise ue = UserExercise.find("id",ueid).first();
		for(String a :an){
			String[] item = a.split("_");
			UserSubject  userSubject = UserSubject.findById(item[0]);
			int cnt =0;
			int index = Integer.parseInt(item[1]);
			
			UserExerciseItem ueItem = ue.userExerciseItem.get(index);
			ueItem.subject = userSubject.subject;
			ueItem.user = user;
			
			for(int i=1;i< item.length;i++){
				Option  option = Option.findById(item[2]);
				ueItem.userAnswer.add(option);
				
				for (Option o :ueItem.subject.answer){
					if(o.equals(option)){
						cnt++;
					}
				}
			}
			ueItem.save();
			ue.userExerciseItem.add(ueItem);
			/***
			 * 4. 用户历史题目信息更新：
				属性更新
					完成次数(complete_count)
					展现次数
					正确次数(correct_count)
					错误次数(mistake_count)
					上一次完成时间(update_at)
					下一次展现时间
					题目入库时间(create_at)
					剩余重复次数(URT=User should Reapet Time)
				展现权重更新
					QS
					if (is 强化考点) +3
					if (abs(U.USS-100*(1-FCR))<10) +2
					if (abs(U.USS-100*(1-FCR))>20) -2
					if (U.KTS == Null) +3
					if (U.KTS > (U.USS+10)) -2
					if (AR==历史题库推荐) +4
			 */
			
			if(ueItem.subject.answer.size() == cnt){
				if(userSubject.displayCount ==0){
					userSubject.isFC =1;
				}else if(userSubject.displayCount ==0 && userSubject.isFC ==0){
					userSubject.isSC =1;
				}
				
				userSubject.o().inc("correctCount, displayCount,completeCount", 1, 1,1).update("_id", userSubject.getId());
//				userSubject.correctCount = userSubject.correctCount+1;
//				userSubject.displayCount = userSubject.displayCount+1;
//				userSubject.completeCount = userSubject.completeCount +1;
				userSubject.URT = userSubject.URT -1;
				correctCnt++;
				ueItem.score = 1;
			}else{
				if(ueItem.subject.answer.size() ==0){
					ueItem.score = -1;
				}else{
					userSubject.o().inc("mistakeCount, displayCount,completeCount", 1, 1,1).update("_id", userSubject.getId());
//					userSubject.mistakeCount = userSubject.mistakeCount+1;
//					userSubject.displayCount = userSubject.displayCount+1;
//					userSubject.completeCount = userSubject.completeCount +1;
					userSubject.URT = userSubject.URT -1;
					mistakeCnt++;
					ueItem.score = 0;
				}
			}
			ueItem.save();
			ue.userExerciseItem.add(ueItem);
			//TODO:下次可以展现的时间
			Date next = new Date(System.currentTimeMillis());
			next = DateUtils.addMinutes(next, 15);
			userSubject.nextDate = next;
			userSubject.updateAt = new Date();
			userSubject.save();
		}	
		ue.doneTime = time;
		ue.score = (correctCnt/ue.userExerciseItem.size())*100;
		ue.save();
		//new UserProfileUdateJob(exercises).now();
		render(ue,correctCnt,mistakeCnt);
	}
}
