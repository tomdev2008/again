package tools;

import play.modules.morphia.Model;
import play.modules.morphia.Seq;



public class SequenceUtils {
	
	public static <T> Long generateLongId(Class<T> clazz) {
        return Seq.nextValue(clazz);
    }
	
	
}
