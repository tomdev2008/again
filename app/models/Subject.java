package models;

import java.util.*;

import models.enums.SubjectStatus;
import models.enums.SubjectType;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;
import tools.SequenceUtils;
import utils.EasyMap;

/**
 * Created with IntelliJ IDEA.
 * User: wji
 * Date: 13-7-11
 * Time: 下午8:25
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class Subject extends BaseModel{

	/**
	 * 题目状态
	 * */
    public SubjectStatus status;

	/**
	 * 题目类型
	 * */
    public SubjectType type;
    
	/**
	 * 题目所属课程
	 * */
    public Course course;
    
	/**
	 * 题目来源
	 * */
    public SubjectSource source;
    
    
	/**
	 * 题目从属大知识点
	 * */
    public Tag bigTag = new Tag();
    
	/**
	 * 题目知识点
	 * */
    public Set<Tag> tags = new HashSet();

    /**
	 * 所在题库的序号
	 * */
    public int index;
    
	/**
	 * 题目描述
	 * */
    public String title;
 
	/**
	 * 题目选项
	 * */
    @Reference
    public List<Option> options = new ArrayList();
   
	/**
	 * 题目答案
	 * */
    @Reference
    public List<Option> answer= new ArrayList();
    
	/**
	 * 大题的子题目
	 * */

    public List<Subject> subs ;

	/**
	 * 题目解答
	 * */
    public String solution;
    
	/**
	 * 题目权重
	 * */
    public int weight=50;

	/**
	 * 题目重复频度
	 * */
    public int frequency = 14;

	/**
	 * 题目的综合正确率
	 * */
    public long  ACR=0;

	/**
	 * 题目一次正确率
	 * */
    public long FCR =0;

	/**
	 * 题目二次正确率
	 * */
    public long SCR =0;

    @Reference
    public User owner;
    
   
}


