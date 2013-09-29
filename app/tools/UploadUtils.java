package tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import play.Logger;
import play.Play;
import play.libs.Codec;
import play.libs.WS;

import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.PutPolicy;

public class UploadUtils {

//	static {
//		 Config.ACCESS_KEY = Play.configuration.getProperty("qiniu.ak");
//	     Config.SECRET_KEY = Play.configuration.getProperty("qiniu.ck");
//	}
	
	public static String upload(File file) throws AuthException, JSONException{
		 Config.ACCESS_KEY = "Qd_tBfMSXMJmaveNsFLKptr7J1eOCCIWYvpqyN_W";
	     Config.SECRET_KEY = "CQSjBUIyeSfnfjoglPEjruV5Zx2CrXemDAvqVnPU";
		Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
        // 请确保该bucket已经存在
        String bucketName = "hizhiti";
        PutPolicy putPolicy = new PutPolicy(bucketName);
        String uptoken = putPolicy.token(mac);
        PutExtra extra = new PutExtra();
        String key = String.valueOf(System.currentTimeMillis());
        PutRet ret = IoApi.putFile(uptoken, key, file, extra);
		return ret.getKey();
	}
	
	public static String copyUrl(String url) throws IOException, AuthException, JSONException{
		File file = getImageFile(url);
		return UploadUtils.upload(file);
	}
    /**
     * 把指定url的图片写到目标文件中，若是这个图片有缓存，则从缓存文件中读
     * @param dest 目标文件
     * @param obj url
     * @throws IOException
     */
    private static File getImageFile(Object obj) throws IOException {
        if (obj == null) return null;
        String url = obj.toString();
        System.out.println(url);
        String prefix=url.substring(url.lastIndexOf("."),url.lastIndexOf("?"));
        File file = new File(FileUtils.getTempDirectory(), "uploads/images/" + Codec.hexMD5(url)+prefix);
        if (file.exists()) {
            Logger.info("write image %s to file from cache file", url);
        } else {
            Logger.info("write image to file from url:%s", url);
            FileUtils.copyInputStreamToFile(getStream(url), file);
        }
        return file;
    }
    
	private static InputStream getStream(String url) throws IOException{

		URL u = new URL(url);  
        //打开链接  
        HttpURLConnection conn = (HttpURLConnection)u.openConnection();  
        //设置请求方式为"GET"  
        conn.setRequestMethod("GET");  
        //超时响应时间为5秒  
        conn.setConnectTimeout(5 * 1000);  
        //通过输入流获取图片数据  
        InputStream inStream = conn.getInputStream();  
        return inStream;

	}
	
	public static void main(String[] args) throws AuthException, JSONException, IOException{
		
	
		File file = getImageFile("http://img1.mobile.360.cn/c0/cdn/baibian/themes/images/crop/240x254/cover_resources_1379858418691_1379858418691.jpg");
		
		UploadUtils.upload(file);
	}
	
	
}
