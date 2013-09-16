package controllers.wither;

import java.util.Map;

import models.User;
import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import utils.EasyMap;
import controllers.admin.Secure;

/**
 * 后台日志记录.
 * @author jiwei
 * @since 2013-7-14
 */
public class LogPrinter extends Controller {
	/**
	 * <p>开始前触发该函数，记录日志.</p>
	 * <p>日志记录格式： 以\002进行分割，第一个参数为请求登陆用户，第二个参数为请求URL路径，第三个参数为请求url参数.</p>
	 */
    @Before
    public static void log() {
        Map<String, String> map = request.params.allSimple();
        map.remove("body");
        User operater = Secure.getLoginUser();
        String userName = "";
        if (operater != null) {
        	userName = operater.userName;
        }
        Logger.info("\002%s\002%s\002%s", userName, request.url, map);
    }
}
