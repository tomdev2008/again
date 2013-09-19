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
	
	@Reference
	public Course course;

	/**本次练习类型*/
	public ExerciseType type;
	
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
	
	public Date createAt;

}
