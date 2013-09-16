package utils;

import play.mvc.Http;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * @author jiwei
 * @since 2013-7-14
 */
public class NetUtils {
    public static String getIp(Http.Request request) {
        Http.Header header = request.headers.get("x-real-ip");
        return (header != null && !utils.StringUtils.isEmpty(header.value())) ? header.value() : request.remoteAddress;
    }

    public static boolean isLocal(String ip) {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            // Check if the address is a valid special local or loop back
            if (addr.isAnyLocalAddress() || addr.isLoopbackAddress())
                return true;

            // Check if the address is defined on any interface
            return NetworkInterface.getByInetAddress(addr) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
