package utils;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import play.Play;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author jiwei
 * @since 2013-7-14
 */
public class JsonHelper {
    private static ObjectMapper mapper = new ObjectMapper();
    static {
        if (!Play.id.toLowerCase().startsWith("prod")) {
            mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        }
    }

    public static String toString(Object obj) throws IOException {
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            mapper.writeValue(os, obj);
            return os.toString("UTF-8");
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    public static <T> T fromString(String str, java.lang.Class<T> valueType) throws java.io.IOException, org.codehaus.jackson.JsonParseException, org.codehaus.jackson.map.JsonMappingException {
        byte[] bytes = str.getBytes("UTF-8");
        return mapper.readValue(bytes, 0, bytes.length, valueType);
    }
}
