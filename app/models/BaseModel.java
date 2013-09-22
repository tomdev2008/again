package models;

import com.google.code.morphia.annotations.Id;

import play.modules.morphia.Model;

public class BaseModel extends Model {

	@Id
	public long id;
	
//	@Override
//    public Object getId() {
//        return id;
//    }
//    
//    @Override
//    protected void setId_(Object id) {
//        id = processId_(id);
//    }
//    
//    protected static Object processId_(Object id) {
//        return id;
//    }
}
