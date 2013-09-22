package tools;

import play.modules.morphia.Model;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.utils.LongIdEntity.StoredId;

public class SequenceUtils {
	
	public static <T> Long generateLongId(Class<T> clazz) {
        String collName = Model.ds(clazz).getCollection(clazz).getName();
        Query<StoredId> q = Model.ds(clazz).find(StoredId.class, "_id", collName);
        UpdateOperations<StoredId> uOps =Model.ds(clazz).createUpdateOperations(StoredId.class).inc("value");
        StoredId newId = Model.ds(clazz).findAndModify(q, uOps);
        if (newId == null) {
        	synchronized(SequenceUtils.class){
        		 if (newId == null){
		            newId = new StoredId(collName);
		            Model.ds(clazz).save(newId);
	            }
            }
        }
        return newId.getValue();
    }
	
	
}
