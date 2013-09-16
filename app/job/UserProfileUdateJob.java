package job;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.ScheduleInfo;
import models.StudentProfile;
import models.Subject;
import models.Tag;
import models.User;
import models.UserExercise;
import play.jobs.Job;
import play.modules.morphia.Model.MorphiaQuery;

public class UserProfileUdateJob extends Job {

	private List<UserExercise> ues;
	public UserProfileUdateJob(List<UserExercise> ues){
		
		this.ues = ues;
	}
	public void doJob() throws Exception {
		if(ues ==null || ues.size() ==0){
			return;
		}
		Tag course = ues.get(0).course;
		User user = ues.get(0).user;
		/***
		 2. 用户Profile信息更新：
			 学科预计分数更新
			 知识点图谱更新
			 知识点属性(新增，预测分数，强化考点？，提升考点？)
		 **/
		MorphiaQuery query = UserExercise.find("user",user).filter("course", course);
		long CR = query.sum("correctCount");
		long All = query.sum("displayCount");
		long USS = CR/All;
		StudentProfile sp = StudentProfile.filter("user", user).filter("courses", course).first();
		sp.courseScore.put(course.getId().toString(), USS);
		sp.save();
		
		Map<Tag,ScheduleInfo> map = new HashMap<Tag,ScheduleInfo>();
		for(UserExercise ue:ues){
			/**
			 1. 题库信息更新：
					题目：属性：FCR(一次正确率)--如果是该用户第一次展现	
					题目：属性：SCR(二次正确率)--如果是该用户第二次展现
					题目：属性：综合正确率
			 */
			Subject sb = ue.subject;
			query = UserExercise.filter("subject",sb);
			long fc = query.sum("isFC");
			long sc = query.sum("isSC");
			long all = query.countAll();
			long ccnt = query.sum("correctCount");
			long complete = query.sum("completeCount");
			long FCR = (fc/all)*100;
			long SCR = (sc/all)*100;
			long ACR = (ccnt/complete)*100;
			sb.FCR= FCR;
			sb.SCR =SCR;
			sb.ACR = ACR;
			sb.save();
			
			Iterator it =ue.subject.tags.iterator();
			int weight =ue.weight;
			while(it.hasNext()){
				Tag t = (Tag)it.next();
				//知识点图谱更新
				//if(t.context.equals()))
				ScheduleInfo sinfo = ScheduleInfo.find("user", user).filter("course", course).filter("tag", t).first();
				if(sinfo ==null){
					continue;
				}
				query = UserExercise.find("user",user).filter("course", course).filter("tags", t);
				long KCR = query.sum("correctCount");
				long KAll = query.sum("displayCount");
				sinfo.KTS =KCR/KAll;
				sinfo.UpdateAt = new Date();
				sinfo.save();
				map.put(t, sinfo);

				/**4展现权重更新
				QS
				if (is 强化考点) +3
				if (abs(U.USS-100*(1-FCR))<10) +2
				if (abs(U.USS-100*(1-FCR))>20) -2
				if (U.KTS == Null) +3
				if (U.KTS > (U.USS+10)) -2   tag  KTS 预测分数
				if (AR==历史题库推荐) +4
				***/
				if(t.info!=null && t.info.containsKey("isImportant")){
					 weight +=3;
				}
				if(sinfo.KTS ==0){
					weight +=3;
				}else if(sinfo.KTS >(USS +10)){
					weight +=2;
				}
				
			}
			
			//更新学习计划的tag属性。
			long value = Math.abs(USS - (100 -sb.FCR));
			if( value<10){
				weight +=2;
			}
			if(value >20){
				weight +=2;
			}
			
			if(ue.AR ==1){
				weight +=4;
			}
			ue.weight = weight;
			ue.save();
			
		}
		


	}
}
