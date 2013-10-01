package models;

import java.util.Date;

import com.google.code.morphia.annotations.Id;

import play.modules.morphia.Model;
import play.modules.morphia.Seq;

public class BaseModel extends Model {

	@Id
	public long id;
	
	public BaseModel(){
		id = Seq.nextValue(this.getClass().getName());
	}
	
    public Date createAt = new Date();

    public Date updateAt = new Date();
}
