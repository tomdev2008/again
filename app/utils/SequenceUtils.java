package utils;

import play.modules.morphia.Model;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.utils.LongIdEntity.StoredId;

public class SequenceUtils {
	
	public static <T> Long generateLongId(Class<T> clazz) {
        String collName = Model.ds().getCollection(clazz).getName();
        Query<StoredId> q = Model.ds().find(StoredId.class, "_id", collName);
        UpdateOperations<StoredId> uOps =Model.ds().createUpdateOperations(StoredId.class).inc("value");
        StoredId newId = Model.ds().findAndModify(q, uOps);
        if (newId == null) {
        	synchronized(SequenceUtils.class){
        		 if (newId == null){
		            newId = new StoredId(collName);
		            Model.ds().save(newId);
	            }
            }
        }
        return newId.getValue();
    }
	
	
}
