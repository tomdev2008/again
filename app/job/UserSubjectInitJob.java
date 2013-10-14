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
			UserCourse uc = UserCourse.find("user", user).filter("course", course).first();
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
			List<Subject> sbs = Subject.find("course", course).filter("source", source).filter("type nin", type).filter("status", SubjectStatus.VALID).asList();
			for(Subject sb:sbs){
				UserSubject usb = new UserSubject();
				usb.subject = sb;
				usb.user = user;
				usb.course = course;
				usb.URT = sb.frequency;
				usb.weight = sb.weight;
				usb.save();
				System.out.println("add usb over");
				for(Tag tag :sb.tags){
					UserTag ut = UserTag.find("user", user).filter("course", course).filter("tag", tag).first();
					
					if(ut == null){
						ut = new UserTag();
						ut.user = user;
						ut.tag = tag;
						ut.subjectCnt =1;
						ut.course = course;
					}else{
						ut.subjectCnt = ut.subjectCnt +1;		
					}
					ut.save();
					System.out.println("add ut over");
					UserTag child = ut;
					while(child.tag.context!=null){
						UserTag parent = UserTag.find("user",user).filter("course", course).filter("tag", child.tag.context).first();
						if(parent!=null){
							parent.subjectCnt = parent.subjectCnt+1;
						}else{
							parent = new UserTag();
							parent.user = user;
							parent.tag = child.tag.context;
							parent.subjectCnt =1;
							parent.course = course;
						}
						parent.save();
						child.context = parent;
						child.save();
						child = parent;
						System.out.println("add parent ut over");
					}
				}
			}
		}
		
		
	}
	
}
