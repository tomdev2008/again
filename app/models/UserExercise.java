package models;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.enums.UserExerciseStatus;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;
import play.modules.morphia.validation.Unique;

@Entity
public class UserExercise extends Model {

	@Reference
	public User user;
	
    @Reference
    public Tag course;
    
    @Reference
    public Tag grade;
    
	@Reference
	public Set<Tag> tags = new HashSet();
	   
	@Reference
	@Unique
	public Subject subject;
	
	@Reference
	public List<Option>  userAnswer;
	
	public UserExerciseStatus status;
	
	/**剩余重复次数(URT=User should Reapet Time)*/
	public int URT=0;
	public int currentScore=0;
	public int displayCount=0;
	public int completeCount=0;
	public int correctCount=0;
	public int mistakeCount=0;
	public int weight=0;
	public int isFC=0;
	public int isSC=0;
	/**
	 * 0 新题  1历史题库  2知识点推荐
	 * **/
	public int AR=0;
	public Date nextDate = new Date();
	public Date subjectCreateAt = new Date();
	public Date createAt = new Date();
    public Date updateAt = new Date();
	
}
