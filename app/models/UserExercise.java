package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.enums.ExerciseType;

import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;
import tools.SequenceUtils;

public class UserExercise extends BaseModel {

	@Reference
	public User user;
	
	public Course course;
	/**
	 * 0 未完成
	 * 1 完成
	 * */
	public int status;
	
	public String name;
	
	
	/**本次练习类型*/
	public ExerciseType type;
	
	/**当类型是真题或者模拟题时，根据paper 中的source来获取取题目*/
	@Reference
	public Paper paper;
		
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
	public List<UserExerciseItem> userExerciseItem = new ArrayList<UserExerciseItem>();
	
	/**
	 * 新涉及知识点
	 */
	public List<Tag> newTag = new ArrayList<Tag>();
	
	/**
	 * 本次练习建议
	 */
	public List<String> suggestion = new ArrayList<String>();
	

}
