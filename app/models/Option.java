package models;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;

import play.modules.morphia.Model;


@Entity
public class Option extends BaseModel {

	
	public String content;
	public Date createAt = new Date();
    public Date updateAt = new Date();
}
