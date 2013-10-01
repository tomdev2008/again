package models;

import java.util.Date;

import play.modules.morphia.Model;
import tools.SequenceUtils;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

@Entity
public class UserCourse extends BaseModel{

	
	@Reference
	public User user;
	
	@Reference
	public Course course;
	
	/**
	 * 预测分
	 */
	public int KTS;
	
	/**
	 * 分数排名比例
	 */
	public int scoreTop;
	
	/**
	 * 完成题量
	 */
	public int done;
	
	/**
	 * 完成题目排名比例
	 */
	public int doneTop;
	
	/**
	 * 练习次数
	 */
	public int doneCnt;
	 
	/**
	 * 练习次数
	 */
	public long doneTime;
	
}
