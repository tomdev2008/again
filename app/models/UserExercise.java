package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.enums.ExerciseType;

import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;

public class UserExercise extends Model {

	@Reference
	public User user;
	
	public Course course;
	/**
	 * 0 未完成
	 * 1 完成
	 * */
	public int status;
	
	/**本次练习类型*/
	public ExerciseType type;
	
	/**当类型是真题或者模拟题时，答题的属性从userpaper 中取*/
	@Reference
	public UserPaper upaper;
	
	/**本次练习名称*/
	public String name;
	
	/**
	 * 本次考试得分
	 */
	public int score;
	
	/**
	 * 本次完成后分数排名比例
	 */
	public int scoreTop;

	/**
	 * 本次完成后完成题目排名比例
	 */
	public int doneTop;
	
	/**
	 * 本地练习时长
	 */
	public long doneTime;
	
	/**
	 * 本地练习题目
	 */
	@Reference
	public List<UserExercise> userExercise= new ArrayList<UserExercise>();
	
	/**
	 * 新涉及知识点
	 */
	public List<Tag> newTag = new ArrayList<Tag>();
	
	/**
	 * 本次练习建议
	 */
	public List<String> suggestion = new ArrayList<String>();
	
	public Date createAt;
	
	public Date updateAt;

}
