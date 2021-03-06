package models;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;

@Entity
public class UserTag extends BaseModel {


	@Reference
	public User user;

	/**
	 * tag所属课程
	 * */
	@Reference
    public Course course;
    
	/**
	 * 父UserTag
	 * */
    @Reference
    public  UserTag  context;
    
	/**
	 * 知识点
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

    
}
