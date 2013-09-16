package utils;

/**
 * @author jiwei
 * @since 2013-7-14
 */
public class MongodbUtils {
    private static String DOT_FLAG = "$DOT$";

    /**
     * <p>格式化Mongodb的Key</p>
     * <p>比如把：key中含有点号的转义成$DOT$</p>
     *
     * @param key key
     * @return 转义后的key，符合mongodb的存储规则
     */
    public static String formatKey(String key) {
        return StringUtils.isBlank(key) ? key : key.replace(".", DOT_FLAG);
    }

    /**
     * 把一个转义过的key还原成原来的key
     * @param key 转义过的key
     * @return 原来的key
     */
    public static String revertKey(String key) {
        return StringUtils.isBlank(key) ? key : key.replace(DOT_FLAG, ".");
    }
}
