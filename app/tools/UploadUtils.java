package tools;

import java.io.File;

import org.json.JSONException;

import play.Play;

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
		return key;
	}
	
	public static void main(String[] args) throws AuthException, JSONException{
		
		File file = new File("/Users/wji/Desktop/ytk/123.jpg");
		UploadUtils.upload(file);
	}
	
	
}
