package tools;


import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import play.Play;
import play.cache.Cache;
import play.templates.JavaExtensions;

/**
 * @author wujinliang
 * @since 9/7/12
 */
public class BizExtensions extends JavaExtensions {

  
    public static String cdn(String fileName) {
        return "http//:";
    }

  
//
//    public static List<String> cdn(List<String> urls, String cropSize) {
//        List<String> result = new ArrayList<String>();
//        for (String url : urls) {
//            result.add(cdn(url, cropSize));
//        }
//        return result;
//    }

    private static String toCropUrl(String url, String cropSize) {
        return url.replace("/origin/", "/crop/" + cropSize + "/").replace(URLEncoder.encode("/origin/"), URLEncoder.encode("/crop/" + cropSize + "/"));
    }


}
