package job;

import java.util.List;

import models.Course;
import models.Subject;
import models.SubjectSource;
import models.Tag;
import models.User;
import models.UserSubject;
import models.UserTag;
import models.enums.SubjectStatus;
import models.enums.SubjectType;
import play.jobs.Job;
import play.modules.morphia.Model.MorphiaQuery;

public class UserSubjectInitJob extends Job{
	private User user;
	private Course course;
	private SubjectSource source;
	public UserSubjectInitJob(User user,Course course,SubjectSource source){
		this.user = user;
		this.course = course;
		this.source = source;
	}
	public void doJob(){
		long cnt = UserSubject.filter("user", user).filter("couser", course).count();
		if(cnt ==0){
			SubjectType[] type = {SubjectType.SUBS,SubjectType.SUBM};
			List<Subject> sbs = Subject.filter("course", course).filter("source", source).filter("type nin", type).filter("status", SubjectStatus.VALID).asList();
			for(Subject sb:sbs){
				UserSubject usb = new UserSubject();
				usb.subject = sb;
				usb.user = user;
				usb.course = course;
				usb.URT = sb.frequency;
				usb.weight = sb.weight;
				usb.save();
				for(Tag tag :sb.tags){
					UserTag ut = UserTag.find("user", user).filter("course", course).filter("tag", sb).first();
					if(ut == null){
						ut = new UserTag();
						ut.bigTag  = sb.bigTag;
						ut.user = user;
						ut.tag = tag;
						ut.subjectCnt =1;
						ut.course = course;
						ut.save();
					}else{
						ut.subjectCnt = ut.subjectCnt +1;
						ut.save();
					}
					
				}
			}
		}
		
		
	}
	
}
