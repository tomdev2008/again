package job;

import java.util.Date;
import java.util.List;

import models.ScheduleInfo;
import models.Subject;
import models.Tag;
import models.User;
import models.UserExercise;
import models.enums.UserExerciseStatus;
import play.jobs.Job;

public class UserExerciseUpdateJob extends Job{

	private User user;
	private Tag course;

	public UserExerciseUpdateJob(User u,Tag course){
		user = u;
		this.course = course;
	}
	public void doJob() throws Exception {
		if(user == null || course == null){
			return;
		}
		/**
		 3. 新题入库：
			根据知识点 
			根据试题相似度 --TODO
			根据协同过滤 --TODO
		 **/
		
		List<ScheduleInfo> sinfos = ScheduleInfo.find("user", user).filter("course", course).asList();
		for(ScheduleInfo info: sinfos){
			UserExercise userExercise = UserExercise.find("user", user).filter("tags", info.tag).order("subjectCreateAt").first();
			List<Subject> sbs =Subject.filter("course", course).filter("tags", info.tag).filter("createAt >", userExercise.subjectCreateAt).asList();
			for(Subject sb :sbs){
				UserExercise ue = new UserExercise();
				ue.course = sb.course;
				ue.grade = sb.grade;
				ue.subject = sb;
				ue.createAt = new Date();
				ue.tags = sb.tags;
				ue.status = UserExerciseStatus.START;
				ue.user = user;
				ue.subjectCreateAt = sb.createAt;
				ue.save();
			}
		}
		
		UserExercise.find("user", user);
		
	}
}
