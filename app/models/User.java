package models;

import java.util.Date;

import models.enums.RoleType;
import models.enums.UserStage;
import models.enums.UserStatus;

import play.libs.Codec;
import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Entity;

@Entity
public class User extends Model {
	
	public UserStatus status;
	public UserStage stage;
	public String userName;		
	public String password;	
	public RoleType roleType;
	public AdminRole adminRole;
	public int gender;
	public Date birth;
	public String email;
	public String headUrl;
	public String provie;
	public String city;
	public String mobile;
	
	public User(){
		
	}
	
	public User(String userName, String password) {
		this.userName = userName;
		this.password = Codec.hexMD5(password);
	}	
	
	public User(String userName, String password, AdminRole adminRole) {
		this.userName = userName;
		this.password = Codec.hexMD5(password);
		this.adminRole = adminRole;
	}
}
