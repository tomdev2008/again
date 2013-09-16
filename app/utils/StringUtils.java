package utils;

import java.io.FileInputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.IOUtils;

public class StringUtils extends org.apache.commons.lang.StringUtils {
	/**
	 * decode string.
	 * 
	 * @param str
	 *            string
	 * @return decoded string
	 */
	public static String decode(final String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (Exception e) {
			return str;
		}
	}
	
	public static String encode(final String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			return str;
		}
	}

    public static String defaultIfEmpty(Object obj, String defaultValue) {
        return obj == null ? defaultValue : obj.toString();
    }

	/**
	 *  <p>只允许字母和数字</p>
	 *  <p>String regEx = "[^a-zA-Z0-9]"</p>
	 *  <p>清除掉所有特殊字符.</p>
	 * @param str 传入的字符串
	 * @return 将过滤好的字符串返回
	 * @throws java.util.regex.PatternSyntaxException
	 */
	public static String stringFilter(final String str)
			throws PatternSyntaxException {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

    /**
     * <p>使用给定的数据<code>data</code>，逐个替换一个文件中的含有data.keySet的内容</p>
     * <p>比如：一个文件的内容是：hello:${pkg}</p>
     * <p>data={pkg:"abc"}</p>
     * <p>那么parseFile("文件名路径", data)==hello:abc</p>
     *
     * @param file 文件名
     * @param data 要替换的数据
     * @return 替换后的内容
     */
    public static String parseFile(String file, Map<String, String> data) {
        return parseFile(file, data, "UTF-8");
    }

    public static String parseFile(String file, Map<String, String> data, String encoding) {
        FileInputStream fis = null;
        String str = null;
        try {
            fis = new FileInputStream(file);
            str = IOUtils.toString(fis, encoding);
            for (String key : data.keySet()) {
                str = str.replace("${" + key + "}", data.get(key));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return str;
    }

    //将127.0.0.1形式的IP地址转换成十进制整数，这里没有进行任何错误处理
    public static long ipToLong(String strIp){
        long[] ip = new long[4];
        //先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        //将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1+1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2+1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3+1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    //将十进制整数形式转换成127.0.0.1形式的ip地址
    public static String longToIP(long longIp){
        StringBuilder sb = new StringBuilder("");
        //直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        sb.append(".");
        //将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        //将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        //将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        return sb.toString();
    }
}
