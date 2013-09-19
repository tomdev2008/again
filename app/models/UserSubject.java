package models;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;

@Entity
public class UserSubject extends Model {
	
	@Reference
	public Subject subject;
	
	/**剩余重复次数(URT=User should Reapet Time)*/
	public int URT=0;
	/**
	 * 显示次数
	 * */
	public int displayCount=0;

	/**
	 * 完成次数
	 * */
	public int completeCount=0;

	/**
	 * 正确次数
	 * */
	public int correctCount=0;

	/**
	 * 错误次数
	 * */
	public int mistakeCount=0;

	/**
	 * 权重
	 * */
	public int weight=0;

	/**
	 * 是否是第一次正确
	 * */
	public int isFC=0;

	/**
	 * 是否是第二次正确
	 * */
	public int isSC=0;
	
	/**
	 * 0 新题  1历史题库  2知识点推荐
	 * **/
	public int AR=0;

	/**
	 * 下次出现时间
	 * */
	public Date nextDate = new Date();
	
	public Date createAt = new Date();
    
	public Date updateAt = new Date();
}
