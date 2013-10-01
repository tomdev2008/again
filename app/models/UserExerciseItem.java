package models;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;
import play.modules.morphia.validation.Unique;

@Entity
public class UserExerciseItem extends BaseModel {
	   

	@Reference
	public User user;
	
	@Reference
	public Subject subject;
	
	@Reference
	public List<Option>  userAnswer;


}