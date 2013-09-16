package service;

import play.libs.Codec;
import models.User;
import models.enums.RoleType;
import models.enums.UserStatus;

public class UserService {

	
	public static User getUser(String userName){
		
		if(User.find("userName", userName).count() >0){
			return User.find("userName", userName).first();
		}
		User user = new User();
		user.userName = userName;
		user.password = Codec.hexMD5("12345");
		user.status = UserStatus.NEWBIE;
		user.roleType = RoleType.ADMIN;
		user.save();
		return user;
		
	}
}
