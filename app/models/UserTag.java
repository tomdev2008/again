package models;

import java.util.Date;

import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;

public class UserTag extends Model {


	@Reference
	public User user;

	/**
	 * tag所属课程
	 * */
    @Reference
    public Course course;
    
	/**
	 * 
	 * */
    @Reference
    public Tag tag;
    
    public int score;
    /**
	 * 一共多少道题目
	 * */
    public int subjectCnt;
    
    /**
	 * 完成多少道题目
	 * */
    public int doneCnt;
    
    /**
	 * 正确率
	 * */
	public int CR=0;

	public Date createAt = new Date();
    
	public Date updateAt = new Date();
    
}
