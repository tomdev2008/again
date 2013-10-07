package job;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Subject;
import models.Tag;
import models.User;
import models.UserCourse;
import models.UserExercise;
import models.UserExerciseItem;
import models.UserSubject;
import models.UserTag;
import play.jobs.Job;
import play.modules.morphia.Model.MorphiaQuery;

public class UserProfileUdateJob extends Job {

	private UserExercise ues;
	public UserProfileUdateJob(UserExercise ues){
		
		this.ues = ues;
	}
	public void doJob() throws Exception {
		if(ues ==null){
			return;
		}
		//根据本次测试的结果，更新 userCourse userTag
		/***
		 2. 用户Profile信息更新：
			 学科预计分数更新
			 知识点图谱更新
			 知识点属性(新增，预测分数，强化考点？，提升考点？)
		 **/
		
		MorphiaQuery query = UserSubject.find("user",ues.user).filter("course", ues.course).filter("displayCount >", 0);
		
		long CR = query.sum("correctCount");
		long All = query.sum("displayCount");
		int USS =(int)(CR/All)*100;
		UserCourse uc = UserCourse.find("user",ues.user).filter("course", ues.course).first();
		uc.KTS = USS;
		uc.doneCnt = (int)UserExercise.find("user",ues.user).filter("course", ues.course).count();
		uc.doneTime = UserExercise.find("user",ues.user).filter("course", ues.course).sum("doneTime");
		uc.save();
		
		List<UserExerciseItem> uesItems = ues.userExerciseItem;
		for(UserExerciseItem item :uesItems){
			for(Tag tag:item.subject.tags){
				Set<Tag> tags = new HashSet();
				tags.add(tag);
				List<Subject> sbs= Subject.filter("course", ues.course).filter("tag", tags).asList();
				MorphiaQuery usQuery=  UserSubject.filter("subject in", sbs).filter("completeCount >", 0);
				long doneCnt = usQuery.sum("completeCount");
				long crCnt = usQuery.sum("correctCount");
				UserTag uTag = UserTag.find("user",ues.user).filter("course", ues.course).filter("tag", tag).first();
				uTag.CR =(int)(crCnt/doneCnt)*100;
				uTag.doneCnt = (int)doneCnt;
				uTag.score = uTag.CR;
				uTag.save();
			}
		}
	

	}
}
