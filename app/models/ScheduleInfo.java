package models;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;

@Entity
public class ScheduleInfo extends Model {

	@Reference
	public User user;
	
	@Reference
	public Tag course;
	
	@Reference
	public Tag grade;
	
	@Reference
	public Tag tag;
	
	/**预测分数*/
	public long KTS;
	
	public Date createAt = new Date();
	
	public Date UpdateAt = new Date();
}
