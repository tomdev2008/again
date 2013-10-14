package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

@Entity
public class City extends BaseModel{

	@Reference
	public City context;

	
	public String name;
	
	public int index;
}
