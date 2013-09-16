package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.AdminRole;
import models.Menu;
import models.User;
import play.cache.Cache;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Crypto;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Scope;
import play.mvc.Util;
import utils.Constant;
import utils.StringUtils;

/**
 * @author jiwei
 * @since 2013-7-14
 */
public class Secure extends Controller {
    // TODO:暂时使用ehcache来缓存Model类对象，但是没法在分布式环境使用，若是使用memcached的话，需要model类有serialVersionUID字段，且classLoader要一致，现在出现了从memcached反序列化的Model类classloader不是PlayClassLoader,会报ClassCast错误
 //   public static EhCacheImpl caches;
   
    /**
     * url.
     */
    private static String LOGIN_URL = "login.html";

    /**
     * 检查要调用的Action方法是否可访问
     * 支持验证链式：token验证以及SSO验证.
     */
    @Before(unless = { "login", "logout", "loginWithXHR", "authenticate" })
    public static void checkAccess() {
    	if (getActionAnnotation(Public.class) != null) {
		    return;
		}
        String username = getCookie(Constant.COOKIE_LOGIN_USER);
        String lastLoginCookie = getCookie(Constant.COOKIE_LAST_LOGIN);
        // 当一个用户在多处登录，且有一处用户退出或修改了密码，其他地方也应该跟着同时退出登录状态，所以根据last_login这个cookie来判断
     //   if (username == null || caches.get(Constant.CACHE_USER_PREFIX + username) == null || lastLoginCookie == null
        if (username == null || lastLoginCookie == null
                || !checkRememberme() || !lastLoginCookie.equals(Cache.get(Constant.CACHE_USER_LAST_LOGIN_PREFIX + username))) {
            // 若是ajax来的, 则直接抛出异常
            if (request.isAjax()) {
                error("action=reload");
            }
            flash.put("url", "GET".equals(request.method) ? request.url : "/");
            login();
        }
        checkFromDB(username);
    }

    public static void login() {
        flash.keep("url");
        render(LOGIN_URL);
    }

    public static void logout() {
        flash.clear();
        session.clear();
        response.removeCookie(Constant.COOKIE_LAST_LOGIN);
        response.removeCookie(Constant.COOKIE_REMEMBER);
        String username = getCookie(Constant.COOKIE_LOGIN_USER);
        //caches.delete(Constant.CACHE_USER_PREFIX + username);
        Cache.delete(Constant.CACHE_USER_LAST_LOGIN_PREFIX + username);
        login();
    }

    public static void authenticate(String name, String password) {
    	boolean matched = User.filter("userName", name).filter("password", Codec.hexMD5(password)).count() > 0;
        if (!matched) {
            flash.error(Messages.get("validation.password.error"));
            login();
        } else {
            storeUserInfo(name);
            redirectToOriginUrl();
        }
    }

    public static void loginWithXHR(String username, String password) {
        List<User> users = User.filter("userName", username).asList();
        
        Map<String, String> result = new HashMap<String, String>();
        result.put("url", flash.get("url"));
        String error = null;
        if (users.size() < 1) {
            error = Messages.get("用户名不存在", username);
        } else if (users.get(0).password != null && !users.get(0).password.equals(Codec.hexMD5(password))) {
            error = Messages.get("密码错误");
        } else {
            storeUserInfo(username);
        }
        if (error != null) result.put("error", error);
        renderJSON(result);
    }

    @Util
    public static User getLoginUser() {
        String username = getCookie(Constant.COOKIE_LOGIN_USER);
        if (username != null) {
            
        	
//        	//Object user = caches.get(Constant.CACHE_USER_PREFIX + username);
//            if (user != null && user instanceof User) {
//                return (User) user;
//            }
            return User.filter("userName", username).first();
        }
        return null;
    }


    
    @Util
    public static User storeUserInfo(String username) {
        List<User> users = User.filter("userName", username).asList();
        if (users.size() < 1) {
        	return null;
        }
        User user = users.get(0);
       // caches.  set(Constant.CACHE_USER_PREFIX + username, user, Time.parseDuration(Scope.COOKIE_EXPIRE));
        String lastLogin = System.currentTimeMillis() + "";
        Cache.set(Constant.CACHE_USER_LAST_LOGIN_PREFIX + username, lastLogin, Scope.COOKIE_EXPIRE);
        // 设置cookie
        response.setCookie(Constant.COOKIE_LOGIN_USER, username, Scope.COOKIE_EXPIRE);
        response.setCookie(Constant.COOKIE_LAST_LOGIN, lastLogin, Scope.COOKIE_EXPIRE);
        response.setCookie(Constant.COOKIE_REMEMBER, Crypto.sign(username) + "-" + username, Scope.COOKIE_EXPIRE);
        return user;
    }

    private static boolean checkFromDB(String username) {
        User user = null;//(User) caches.get(Constant.CACHE_USER_PREFIX + username);
        if (user == null) user = storeUserInfo(username);
        final String url = StringUtils.substringBefore(request.url, "?");
        if (Menu.filter("url", url).count() < 1) {
        	return true;
        }
        if (user != null && !request.isAjax() && !url.equals("/")) {
            final AdminRole role = user.adminRole;
            Set<Menu> menus = role.menuList;
            for (Menu menu : menus) {
            	if (menu.url != null && menu.url.equals(url)) {
            		return true;
            	}
            }
            forbidden("你当前无权访问该页面");
        }
        return false;
    }

    private static void redirectToOriginUrl() {
        String url = flash.get("url");
        if(url == null) {
            url = "/";
        }
        redirect(url);
    }

    private static String getCookie(String cookieName) {
        Http.Cookie cookie = request.cookies.get(cookieName);
        return cookie != null ? cookie.value : null;
    }

    private static boolean checkRememberme() {
        String remember = getCookie("rememberme");
        if(remember != null && remember.indexOf("-") > 0) {
            String sign = remember.substring(0, remember.indexOf("-"));
            String username = remember.substring(remember.indexOf("-") + 1);
            return Crypto.sign(username).equals(sign);
        }
        return false;
    }
}