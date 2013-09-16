package controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Util;
import play.mvc.With;
import utils.Constant;
@With(Secure.class)
public class Users extends Controller {

	
	
	public static void register(){
		render();
	}
	
	
	
	
    @Util
    public static User getLoginUser() {
        String username = getCookie(Constant.COOKIE_LOGIN_USER);
        if (username != null) {
            return User.filter("userName", username).first();
        }
        return null;
    }
    
    
    private static String getCookie(String cookieName) {
        Http.Cookie cookie = request.cookies.get(cookieName);
        return cookie != null ? cookie.value : null;
    }
}
