package job;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.City;
import models.Course;
import models.Subject;
import models.SubjectSource;
import models.Tag;
import models.User;
import models.UserCourse;
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
	private City city;
	public UserSubjectInitJob(User user,City city,Course course,SubjectSource source){
		this.user = user;
		this.course = course;
		this.source = source;
		this.city = city;
	}
	public void doJob(){
		Set<City> cities = new HashSet<City>();
		cities.add(city);
		long cnt = UserSubject.filter("user", user).filter("course", course).count();
		if(cnt ==0){
			UserCourse uc = UserCourse.find("user", user).filter("course.id", course.id).first();
			if(uc == null){
				uc = new UserCourse();
				uc.cities = cities;
				uc.user = user;
				uc.course = course;
				uc.save();
			}else{
				if(uc.cities.contains(city)){
					//return;
				}else{
					uc.cities.add(city);
					uc.save();
				}
			}
			
			SubjectType[] type = {SubjectType.SUBS,SubjectType.SUBM};
			List<Subject> sbs = Subject.find("course.id", course.id).filter("source.id", source.id).filter("type nin", type).filter("status", SubjectStatus.VALID).asList();
			for(Subject sb:sbs){
				UserSubject usb = new UserSubject();
				usb.subject = sb;
				usb.user = user;
				usb.course = course;
				usb.URT = sb.frequency;
				usb.weight = sb.weight;
				usb.save();
				for(Tag tag :sb.tags){
					UserTag ut = UserTag.find("user", user).filter("course.id", course.id).filter("tag.id", sb.id).first();
					UserTag child = ut;
					if(ut == null){
						ut = new UserTag();
						ut.user = user;
						ut.tag = tag;
						ut.subjectCnt =1;
						ut.course = course;
					}else{
						ut.subjectCnt = ut.subjectCnt +1;		
					}
					while(tag.context!=null){
						UserTag parent = UserTag.find("user",user).filter("course.id", course.id).filter("tag.id", child.tag.context.id).first();
						if(parent!=null){
							child.context = parent;
							parent.subjectCnt = parent.subjectCnt+1;
						}else{
							parent.user = user;
							parent.tag = tag;
							parent.subjectCnt =1;
							parent.course = course;
						}
						parent.save();
						child = parent;
					}
					
					ut.save();
					
					
				}
			}
		}
		
		
	}
	
}
